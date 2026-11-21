"""조약돌 효과음 생성: 묵직한 돌 두 개가 "톡, 톡" 부딪히는 소리 (44.1kHz, mono, 16-bit WAV).
python3 scripts/make_pebble.py  → public/assets/pebble.wav, android/app/src/main/res/raw/pebble.wav
"""
import wave, os
import numpy as np

SR = 44100
rng = np.random.default_rng(7)

def click(t0, gain, f_hi, f_body, dur=0.16):
    n = int(dur * SR)
    t = np.arange(n) / SR
    # 단단한 표면의 고음 "틱" — 빠르게 떨어지는 주파수
    f = f_hi * (1 - 0.18 * (1 - np.exp(-t * 60)))
    tick = np.sin(2 * np.pi * np.cumsum(f) / SR) * np.exp(-t * 120)
    # 돌의 몸통 울림 (부드러운 중음)
    body = np.sin(2 * np.pi * f_body * t) * np.exp(-t * 32) * 0.8
    body += np.sin(2 * np.pi * f_body * 1.48 * t) * np.exp(-t * 45) * 0.3
    # 묵직함: 낮은 쿵 (손바닥 위 돌의 무게감)
    body += np.sin(2 * np.pi * f_body * 0.42 * t) * np.exp(-t * 28) * 0.55
    # 접촉 순간의 짧은 마찰음
    noise = rng.standard_normal(n) * np.exp(-t * 700) * 0.25
    noise = np.convolve(noise, np.ones(10) / 10, mode='same')  # 고음을 깎아 둔탁하게
    # 부드러운 어택 (클릭 노이즈 방지)
    attack = np.minimum(1, t / 0.0015)
    return int(t0 * SR), (tick * 0.3 + body + noise) * attack * gain

total = np.zeros(int(0.38 * SR))
for t0, g, hi, bd in [(0.0, 1.0, 1700, 520), (0.095, 0.5, 1900, 590)]:
    i, sig = click(t0, g, hi, bd)
    total[i:i + len(sig)] += sig[: len(total) - i]

# 끝을 자연스럽게 페이드
fade = int(0.06 * SR)
total[-fade:] *= np.linspace(1, 0, fade)
total = total / np.max(np.abs(total)) * 0.7
pcm = (total * 32767).astype('<i2').tobytes()

for path in ['public/assets/pebble.wav', 'android/app/src/main/res/raw/pebble.wav']:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with wave.open(path, 'wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(pcm)
    print(path, os.path.getsize(path), 'bytes')
