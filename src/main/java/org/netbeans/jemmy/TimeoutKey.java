/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy;

public enum TimeoutKey {
    JMenuOperator_WaitPopupTimeout(60_000L),
    JMenuOperator_PushMenuTimeout(60_000L),
    EventDispatcher_RobotAutoDelay(10L),
    EventTool_WaitEventTimeout(60_000L),
    EventTool_WaitNoEventTimeout(180_000L),
    EventTool_EventCheckingDelta(10L),
    QueueTool_WaitQueueEmptyTimeout(180_000L),
    QueueTool_QueueCheckingDelta(10L),
    QueueTool_InvocationTimeout(180_000L),
    QueueTool_PreInvocationTimeout(160_000L),
    ActionProducer_MaxActionTime(10_000L),
    // 5000 rather than upstream's 1000: native AWT push-scrolling on Windows can
    // legitimately stall past 1s under load before recovering
    AbstractScrollDriver_FreezeTimeout(5_000L),
    ComponentOperator_PushKeyTimeout(0L),
    ComponentOperator_MouseClickTimeout(10L),
    ComponentOperator_BeforeDragTimeout(0L),
    ComponentOperator_AfterDragTimeout(0L),
    ComponentOperator_WaitComponentEnabledTimeout(60_000L),
    ComponentOperator_WaitFocusTimeout(60_000L),
    ComponentOperator_WaitComponentTimeout(10_000L),
    JScrollBarOperator_WholeScrollTimeout(60_000L),
    JScrollBarOperator_DragAndDropScrollingDelta(0L),
    JSliderOperator_WholeScrollTimeout(60_000L),
    JSliderOperator_ScrollingDelta(0L),
    JSpinnerOperator_WholeScrollTimeout(60_000L),
    JSplitPaneOperator_WholeScrollTimeout(60_000L),
    JTextComponentOperator_ChangeCaretPositionTimeout(60_000L),
    JTextComponentOperator_TypeTextTimeout(60_000L),
    JToolTipOperator_WaitToolTipTimeout(60_000L),
    JTreeOperator_BeforeEditTimeout(1_000L),
    JTreeOperator_WaitEditingTimeout(60_000L),
    JTreeOperator_WaitNodeVisibleTimeout(1_000L),
    JTreeOperator_WaitNextNodeTimeout(1_000L),
    ScrollbarOperator_WholeScrollTimeout(60_000L),
    ScrollbarOperator_DragAndDropScrollingDelta(0L),
    TextComponentOperator_BetweenKeysTimeout(0L),
    TextComponentOperator_ChangeCaretPositionTimeout(60_000L),
    TextComponentOperator_TypeTextTimeout(60_000L),
    MouseVisualiser_BeforeClickTimeout(100L),
    WindowManager_TimeDelta(1_000L),
    DialogWaiter_WaitDialogTimeout(10_000L),
    FrameWaiter_WaitFrameTimeout(60_000L),
    Waiter_TimeDelta(50L),
    Waiter_WaitingTime(60_000L),
    WindowWaiter_WaitWindowTimeout(60_000L),
    Apple_SystemMenuDelay(100L),
    JScrollBar_Jump(0L),
    TextApiDriver_EnterText(0L),
    TextKeyboardDriver_EnterText(0L),
    QueueJMenuDriver_OneReleaseDelta(100L),

    // Jelly
    WidgetOperator_WaitWidgetTimeout(0L),
    WizardOperator_WaitWizardStepTimeout(0L),
    OptionsOperator_BeforeEditingTimeout(0L),
    Action_WaitAfterShortcutTimeout(0L),

    // Testing
    Testing_A(0L),
    Testing_B(0L),
    Testing_C(0L),
    Testing_D(0L);

    private final long defaultValue;

    TimeoutKey(long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public long getDefaultValue() {
        return defaultValue;
    }
}
