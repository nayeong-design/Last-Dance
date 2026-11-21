import { useEffect, useRef } from 'react';
import type { CopyEdit } from './useDesignOverrides';

/**
 * 문구 수정 모드.
 * - editing=true: 글자를 클릭하면 그 요소만 편집 가능해지고, 클릭으로 화면이 넘어가지 않게 막는다.
 * - 수정된 문구는 before→after 로 기록되고, 화면을 옮겨 다녀도 MutationObserver가 계속 다시 적용한다.
 */
export function useCopyEditing(opts: {
  editing: boolean;
  edits: CopyEdit[];
  screen: string;
  onEdit: (e: CopyEdit) => void;
}) {
  const { editing, edits, screen, onEdit } = opts;
  // 요소 단위로 원래 문구를 기억한다 (전체 선택 후 입력하면 텍스트 노드가 새로 만들어지기 때문)
  const originals = useRef(new WeakMap<Element, string>());
  const latest = useRef({ edits, screen, onEdit });
  latest.current = { edits, screen, onEdit };

  // 1) 기록된 수정 사항을 화면에 계속 적용
  useEffect(() => {
    const stage = document.querySelector<HTMLElement>('.stage');
    if (!stage) return;
    const apply = () => {
      const map = new Map(latest.current.edits.map((e) => [e.before, e.after]));
      if (map.size === 0) return;
      const walker = document.createTreeWalker(stage, NodeFilter.SHOW_TEXT);
      for (let n = walker.nextNode() as Text | null; n; n = walker.nextNode() as Text | null) {
        const v = n.nodeValue ?? '';
        const after = map.get(v.trim());
        if (after !== undefined && v.trim() !== after) {
          const el = n.parentElement;
          if (el && !originals.current.has(el)) originals.current.set(el, v.trim());
          n.nodeValue = v.replace(v.trim(), after);
        }
      }
    };
    apply();
    const mo = new MutationObserver(apply);
    mo.observe(stage, { childList: true, subtree: true, characterData: true });
    return () => mo.disconnect();
  }, [edits]);

  // 2) 편집 모드: 글자를 클릭하면 그 요소만 편집 가능해지고 전체가 선택된다
  useEffect(() => {
    const stage = document.querySelector<HTMLElement>('.stage');
    if (!stage || !editing) return;
    stage.classList.add('stage--copy-edit');
    let current: HTMLElement | null = null;

    const finish = () => {
      if (!current) return;
      current.removeAttribute('contenteditable');
      current.classList.remove('is-editing');
      current = null;
    };
    const onClick = (e: MouseEvent) => {
      e.stopPropagation(); // 수정 중에는 화면 이동 막기
      e.preventDefault();
      const t = e.target as HTMLElement;
      if (t === current) return;
      // 글자만 담은 가장 안쪽 요소만 편집 대상
      if (!t || t.childElementCount > 0 || !(t.textContent ?? '').trim() || t === stage) return;
      finish();
      current = t;
      if (!originals.current.has(t)) originals.current.set(t, (t.textContent ?? '').trim());
      t.contentEditable = 'true';
      t.spellcheck = false;
      t.classList.add('is-editing');
      t.focus();
      const range = document.createRange();
      range.selectNodeContents(t);
      const sel = window.getSelection();
      sel?.removeAllRanges();
      sel?.addRange(range);
    };
    const onInput = (e: Event) => {
      const el = e.target as HTMLElement;
      const orig = originals.current.get(el);
      if (!orig) return;
      latest.current.onEdit({ screen: latest.current.screen, before: orig, after: (el.textContent ?? '').trim() });
    };
    const onKey = (e: KeyboardEvent) => {
      if (!current) return;
      if (e.key === 'Enter' || e.key === 'Escape') {
        e.preventDefault();
        current.blur();
      }
    };
    const onBlur = (e: FocusEvent) => e.target === current && finish();
    const swallow = (e: Event) => e.stopPropagation();

    stage.addEventListener('click', onClick, true);
    stage.addEventListener('pointerdown', swallow, true);
    stage.addEventListener('input', onInput);
    stage.addEventListener('keydown', onKey);
    stage.addEventListener('focusout', onBlur);
    return () => {
      finish();
      stage.classList.remove('stage--copy-edit');
      stage.removeEventListener('click', onClick, true);
      stage.removeEventListener('pointerdown', swallow, true);
      stage.removeEventListener('input', onInput);
      stage.removeEventListener('keydown', onKey);
      stage.removeEventListener('focusout', onBlur);
    };
  }, [editing]);
}
