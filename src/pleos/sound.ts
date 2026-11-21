/**
 * IVI 알림음 (earcon).
 * 실제 차량에서는 Pleos/AAOS 오디오 포커스(알림 채널)를 통해 재생해야 한다.
 * 웹에서는 Web Audio로 짧게 재생하고, 브라우저 자동재생 정책 때문에 사용자 터치(알리기 버튼)에서 unlock() 한다.
 */
let ctx: AudioContext | null = null;
let pebble: AudioBuffer | null = null;
let loading: Promise<void> | null = null;

function load() {
  if (!ctx) return Promise.resolve();
  loading ??= fetch('assets/pebble.wav')
    .then((r) => r.arrayBuffer())
    .then((b) => ctx!.decodeAudioData(b))
    .then((buf) => {
      pebble = buf;
    })
    .catch(() => {
      loading = null; // 다음 터치에서 다시 시도
    });
  return loading;
}

export const earcon = {
  /** 사용자 제스처 안에서 호출 */
  unlock() {
    try {
      ctx ??= new AudioContext();
      if (ctx.state === 'suspended') void ctx.resume();
      void load();
    } catch {
      /* 오디오 미지원 환경 — 소리 없이 진행 */
    }
  },
  /** 응원 반응 수신음: 조약돌 "톡, 톡". 매번 음높이를 살짝 바꿔 기계적으로 들리지 않게 */
  pebble(volume = 0.6) {
    if (!ctx || !pebble || ctx.state !== 'running') return;
    const src = ctx.createBufferSource();
    src.buffer = pebble;
    src.playbackRate.value = 0.92 + Math.random() * 0.16;
    const g = ctx.createGain();
    g.gain.value = volume;
    src.connect(g).connect(ctx.destination);
    src.start();
  },
};
