/********************************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 ********************************************************************************/

const KEY_NAMES = { del: 'delete', space: ' ' };

/**
 * Checks whether a keydown event is the given hotkey, e.g., <code>'mod+s'</code> or <code>'del'</code>.
 * As with the formerly used angular2-hotkeys, <code>mod</code> is Ctrl (or Cmd on macOS)
 * and hotkeys are ignored while the user is typing in a form field.
 */
export function isHotkey(event: KeyboardEvent, hotkey: string): boolean {
    const target = event.target as HTMLElement;
    if (['INPUT', 'SELECT', 'TEXTAREA'].indexOf(target.nodeName) > -1 || target.isContentEditable) {
        return false;
    }
    const mod = hotkey.startsWith('mod+');
    const key = mod ? hotkey.substring('mod+'.length) : hotkey;
    return (event.ctrlKey || event.metaKey) === mod && !event.shiftKey && !event.altKey
        && (event.key || '').toLowerCase() === (KEY_NAMES[key] || key);
}
