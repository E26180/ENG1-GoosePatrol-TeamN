"""Synthesises the game's sound effects as small WAV files in assets/audio.

Usage: python3 tools/make_sounds.py assets/audio   (standard library only)
Every sound is generated from simple waveforms, so there is no third-party audio.
"""
import math
import random
import struct
import sys
import wave

RATE = 22050
out = sys.argv[1]
random.seed(7)


def save(name, samples):
    peak = max(1e-9, max(abs(s) for s in samples))
    with wave.open(f"{out}/{name}.wav", "wb") as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(RATE)
        f.writeframes(b"".join(struct.pack("<h", int(32000 * 0.8 * s / peak)) for s in samples))


def envelope(i, n, attack=0.01, release=0.08):
    t = i / RATE
    length = n / RATE
    a = min(1.0, t / attack) if attack > 0 else 1.0
    r = min(1.0, (length - t) / release) if release > 0 else 1.0
    return max(0.0, min(a, r))


def tone(freq_at, seconds, shape="sine", attack=0.01, release=0.08, vibrato=0.0):
    n = int(seconds * RATE)
    phase = 0.0
    samples = []
    for i in range(n):
        t = i / RATE
        f = freq_at(t) * (1 + vibrato * math.sin(2 * math.pi * 28 * t))
        phase += f / RATE
        p = phase % 1.0
        if shape == "saw":
            v = 2 * p - 1
        elif shape == "square":
            v = 1.0 if p < 0.5 else -1.0
        else:
            v = math.sin(2 * math.pi * p)
        samples.append(v * envelope(i, n, attack, release))
    return samples


def silence(seconds):
    return [0.0] * int(seconds * RATE)


def mix(*parts):
    n = max(len(p) for p in parts)
    return [sum(p[i] for p in parts if i < len(p)) for i in range(n)]


# Honk: two nasal sawtooth honks whose pitch falls a little (QR_ALERT).
def honk_once():
    saw = tone(lambda t: 330 - 140 * t, 0.22, "saw", 0.01, 0.06, vibrato=0.03)
    nasal = tone(lambda t: 660 - 280 * t, 0.22, "square", 0.01, 0.06)
    return mix(saw, [0.25 * s for s in nasal])
save("honk", honk_once() + silence(0.05) + honk_once())

# Eat: three short crunchy noise bursts.
crunch = []
for _ in range(3):
    n = int(0.05 * RATE)
    crunch += [random.uniform(-1, 1) * envelope(i, n, 0.002, 0.03) for i in range(n)] + silence(0.035)
save("eat", crunch)

# Hidden event: a quick rising arpeggio.
notes = [523.25, 659.25, 783.99, 1046.5]
save("event", sum((tone(lambda t, f=f: f, 0.09, "sine", 0.005, 0.04) for f in notes), []))

# New wave: two horn notes.
save("wave", tone(lambda t: 392, 0.18, "square", 0.01, 0.05) + silence(0.04) + tone(lambda t: 523.25, 0.3, "square", 0.01, 0.12))

# Win: a short fanfare.
fanfare = [(523.25, 0.12), (659.25, 0.12), (783.99, 0.12), (1046.5, 0.4)]
save("win", sum((tone(lambda t, f=f: f, d, "sine", 0.005, 0.05) for f, d in fanfare), []))

# Lose: a falling "wah wah".
save("lose", tone(lambda t: 392 - 40 * t, 0.35, "saw", 0.02, 0.1, vibrato=0.02) + silence(0.05)
     + tone(lambda t: 330 - 80 * t, 0.7, "saw", 0.02, 0.3, vibrato=0.02))
print("wrote honk, eat, event, wave, win, lose")
