import { CHECKLIST, useStore } from '../state/store';
import { PrimaryButton } from '../components/ui';
import { CheckOn, DotIdle } from '../components/icons';
import s from './Checklist.module.css';

/** Figma 354:2555 — Last Dance 코스 선택 모달 */
export default function Checklist() {
  const { s: st, dispatch, go, back } = useStore();
  return (
    <div className={s.scrim} onClick={back}>
      <div className={s.modal} role="dialog" aria-modal aria-labelledby="ld-check-title" onClick={(e) => e.stopPropagation()}>
        <h2 id="ld-check-title" className="t-headline-s-xbold" style={{ whiteSpace: 'nowrap' }}>
          Last Dance를 선택해
          <br />
          차량과의 이별주행을 시작하세요!
        </h2>
        <ul className={s.list}>
          {CHECKLIST.map((c) => {
            const on = st.selected.includes(c.id);
            return (
              <li key={c.id}>
                <button className={s.item} role="checkbox" aria-checked={on} onClick={() => dispatch({ type: 'toggle', id: c.id })}>
                  {on ? <CheckOn /> : <DotIdle />}
                  <span className={s.text}>
                    <span className="t-title-m-semibold" style={{ color: 'var(--p-white)' }}>
                      {c.title}
                    </span>
                    <span className="t-body-s-regular" style={{ color: 'var(--text-secondary)' }}>
                      {c.place}
                    </span>
                  </span>
                </button>
              </li>
            );
          })}
        </ul>
        <PrimaryButton
          disabled={st.selected.length === 0}
          style={{ width: '100%' }}
          onClick={() => {
            dispatch({ type: 'startRecording' });
            go('driving');
          }}
        >
          시작하기
        </PrimaryButton>
      </div>
    </div>
  );
}
