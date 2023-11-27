#version 150

uniform vec2 ScreenSize;
uniform float GameTime;

in float gameTime;
in vec4 vertexColor;
in vec4 normal;

out vec4 fragColor;
// The Universe Within - by Martijn Steinrucken aka BigWings 2018
// Email:countfrolic@gmail.com Twitter:@The_ArtOfCode
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// After listening to an interview with Michael Pollan on the Joe Rogan
// podcast I got interested in mystic experiences that people seem to
// have when using certain psycoactive substances.
//
// For best results, watch fullscreen, with music, in a dark room.
//
// I had an unused 'blockchain effect' lying around and used it as
// a base for this effect. Uncomment the SIMPLE define to see where
// this came from.
//
// Use the mouse to get some 3d parallax.

//
// YouTube video of this effect:
// https://youtu.be/GAhu4ngQa48
//
// YouTube Tutorial for this effect:
// https://youtu.be/3CycKKJiwis


// note from walksanator here:
// I made it PURPLE and "adapted" the values that MC provides

//re-defining shaderlab uniforms in terms of minecraft uniforms
#define iTime (GameTime * 1200.)
#define iMouse vec4(0.0,0.0,0.0,0.0)
#define fragCoord (gl_FragCoord.xy)
#define iResolution ScreenSize

#define S(a, b, t) smoothstep(a, b, t)
#define NUM_LAYERS 4.

float N21(vec2 p) {
    vec3 a = fract(vec3(p.xyx) * vec3(213.897, 653.453, 253.098));
    a += dot(a, a.yzx + 79.76);
    return fract((a.x + a.y) * a.z);
}

vec2 GetPos(vec2 id, vec2 offs, float t) {
    float n = N21(id+offs);
    float n1 = fract(n*10.);
    float n2 = fract(n*100.);
    float a = t+n;
    return offs + vec2(sin(a*n1), cos(a*n2))*.4;
}

float GetT(vec2 ro, vec2 rd, vec2 p) {
    return dot(p-ro, rd);
}

float LineDist(vec3 a, vec3 b, vec3 p) {
    return length(cross(b-a, p-a))/length(p-a);
}

float df_line( in vec2 a, in vec2 b, in vec2 p)
{
    vec2 pa = p - a, ba = b - a;
    float h = clamp(dot(pa,ba) / dot(ba,ba), 0., 1.);
    return length(pa - ba * h);
}

float line(vec2 a, vec2 b, vec2 uv) {
    float r1 = .04;
    float r2 = .01;

    float d = df_line(a, b, uv);
    float d2 = length(a-b);
    float fade = S(1.5, .5, d2);

    fade += S(.05, .02, abs(d2-.75));
    return S(r1, r2, d)*fade;
}

float NetLayer(vec2 st, float n, float t) {
    vec2 id = floor(st)+n;

    st = fract(st)-.5;

    vec2 p[9];
    int i=0;
    for(float y=-1.; y<=1.; y++) {
        for(float x=-1.; x<=1.; x++) {
            p[i++] = GetPos(id, vec2(x,y), t);
        }
    }

    float m = 0.;
    float sparkle = 0.;

    for(int i=0; i<9; i++) {
        m += line(p[4], p[i], st);

        float d = length(st-p[i]);

        float s = (.005/(d*d));
        s *= S(1., .7, d);
        float pulse = sin((fract(p[i].x)+fract(p[i].y)+t)*5.)*.4+.6;
        pulse = pow(pulse, 20.);

        s *= pulse;
        sparkle += s;
    }

    m += line(p[1], p[3], st);
    m += line(p[1], p[5], st);
    m += line(p[7], p[5], st);
    m += line(p[7], p[3], st);

    float sPhase = (sin(t+n)+sin(t*.1))*.25+.5;
    sPhase += pow(sin(t*.1)*.5+.5, 50.)*5.;
    m += sparkle*sPhase;//(*.5+.5);

    return m;
}

void main3() {
    // Normalized pixel coordinates (from 0 to 1)
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;

    // Time varying pixel color
    vec3 col = uv.xyx;

    // Output to screen
    fragColor = vec4(col,1.0);
}

void main()
{
    vec2 uv = (fragCoord-ScreenSize.xy*.5)/ScreenSize.y;
    vec2 M = iMouse.xy / ScreenSize.xy-.5;

    float t = iTime*.1;

    float s = sin(t);
    float c = cos(t);
    mat2 rot = mat2(c, -s, s, c);
    vec2 st = uv*rot;
    M *= rot*2.;

    float m = 0.;
    for(float i=0.; i<1.; i+=1./NUM_LAYERS) {
        float z = fract(t+i);
        float size = mix(15., 1., z);
        float fade = S(0., .6, z)*S(1., .8, z);

        m += fade * NetLayer(st*size-M*z, i, iTime);
    }

    vec3 baseCol = vec3(0.4,0,0.6); //vec3(s, cos(t*.4), -sin(t*.24))*.4+.6;
    vec3 col = baseCol*m;

    #ifdef SIMPLE
    uv *= 10.;
    col = vec3(1)*NetLayer(uv, 0., iTime);
    uv = fract(uv);
    //if(uv.x>.98 || uv.y>.98) col += 1.;
    #else
    col *= 1.-dot(uv,uv);
    t = mod(iTime, 230.);
    col *= S(0., 20., t)*S(224., 200., t);
    #endif

    fragColor = vec4(col,1);
}
