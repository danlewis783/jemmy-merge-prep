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
    JMenuOperator_WaitBeforePopupTimeout(0L),
    JMenuOperator_WaitPopupTimeout(60000L),
    JMenuOperator_PushMenuTimeout(60000L),
    EventDispatcher_RobotAutoDelay(10L),
    EventTool_WaitEventTimeout(60000L),
    EventTool_WaitNoEventTimeout(180000L),
    EventTool_EventCheckingDelta(10L),
    QueueTool_WaitQueueEmptyTimeout(180000L),
    QueueTool_QueueCheckingDelta(10L),
    QueueTool_InvocationTimeout(180000L),
    QueueTool_PreInvocationTimeout(160000L),
    ActionProducer_MaxActionTime(10000L),
    // 5000 rather than upstream's 1000: native AWT push-scrolling on Windows can
    // legitimately stall past 1s under load before recovering
    AbstractScrollDriver_FreezeTimeout(5000L),
    ComponentOperator_PushKeyTimeout(0L),
    ComponentOperator_MouseClickTimeout(10L),
    ComponentOperator_BeforeDragTimeout(0L),
    ComponentOperator_AfterDragTimeout(0L),
    ComponentOperator_WaitComponentEnabledTimeout(60000L),
    ComponentOperator_WaitFocusTimeout(60000L),
    ComponentOperator_WaitComponentTimeout(10000L),
    JComboBoxOperator_BeforeSelectingTimeout(0L),
    JScrollBarOperator_WholeScrollTimeout(60000L),
    JScrollBarOperator_DragAndDropScrollingDelta(0L),
    JSliderOperator_WholeScrollTimeout(60000L),
    JSliderOperator_ScrollingDelta(0L),
    JSpinnerOperator_WholeScrollTimeout(60000L),
    JSplitPaneOperator_WholeScrollTimeout(60000L),
    JTextComponentOperator_ChangeCaretPositionTimeout(60000L),
    JTextComponentOperator_TypeTextTimeout(60000L),
    JToolTipOperator_WaitToolTipTimeout(60000L),
    JTreeOperator_BeforeEditTimeout(1000L),
    JTreeOperator_WaitEditingTimeout(60000L),
    JTreeOperator_WaitNodeVisibleTimeout(1000L),
    JTreeOperator_WaitNextNodeTimeout(1000L),
    ScrollbarOperator_WholeScrollTimeout(60000L),
    ScrollbarOperator_DragAndDropScrollingDelta(0L),
    TextComponentOperator_BetweenKeysTimeout(0L),
    TextComponentOperator_ChangeCaretPositionTimeout(60000L),
    TextComponentOperator_TypeTextTimeout(60000L),
    MouseVisualiser_BeforeClickTimeout(100L),
    WindowManager_TimeDelta(1000L),
    DialogWaiter_WaitDialogTimeout(10000L),
    FrameWaiter_WaitFrameTimeout(60000L),
    Waiter_TimeDelta(50L),
    Waiter_WaitingTime(60000L),
    WindowWaiter_WaitWindowTimeout(60000L),
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

    private long defaultValue;

    TimeoutKey(long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public long getDefaultValue() {
        return defaultValue;
    }
}
