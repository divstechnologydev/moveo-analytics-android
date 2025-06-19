package one.moveo.androidlib;

import lombok.Getter;

public class Constants {

    public static String libVersion="1.0.12";

    public enum Environment {
        DEVELOPMENT("development"),
        PRODUCTION("production");

        private final String value;

        public String getValue() {
            return value;
        }

        Environment(String value) {
            this.value = value;
        }
    }

    public enum MoveoOneEventType {
        START_SESSION("start_session"),
        TRACK("track"),
        UPDATE_METADATA("update_metadata");

        private final String value;

        public String getValue() {
            return value;
        }

        MoveoOneEventType(String value) {
            this.value = value;
        }

    }

    public enum MoveoOneType {
        BUTTON("button"),
        TEXT("text"),
        TEXT_EDIT("textEdit"),
        IMAGE("image"),
        IMAGES("images"),
        IMAGE_SCROLL_HORIZONTAL("image_scroll_horizontal"),
        IMAGE_SCROLL_VERTICAL("image_scroll_vertical"),
        PICKER("picker"),
        SLIDER("slider"),
        SWITCH_CONTROL("switchControl"),
        PROGRESS_BAR("progressBar"),
        CHECKBOX("checkbox"),
        RADIO_BUTTON("radioButton"),
        TABLE("table"),
        COLLECTION("collection"),
        SEGMENTED_CONTROL("segmentedControl"),
        STEPPER("stepper"),
        DATE_PICKER("datePicker"),
        TIME_PICKER("timePicker"),
        SEARCH_BAR("searchBar"),
        WEB_VIEW("webView"),
        SCROLL_VIEW("scrollView"),
        ACTIVITY_INDICATOR("activityIndicator"),
        VIEW("view"),
        VIDEO("video"),
        VIDEO_PLAYER("videoPlayer"),
        AUDIO_PLAYER("audioPlayer"),
        MAP("map"),
        TAB_BAR("tabBar"),
        TAB_BAR_PAGE("tabBarPage"),
        TAB_BAR_PAGE_TITLE("tabBarPageTitle"),
        TAB_BAR_PAGE_SUBTITLE("tabBarPageSubtitle"),
        TOOLBAR("toolbar"),
        ALERT("alert"),
        ALERT_TITLE("alertTitle"),
        ALERT_SUBTITLE("alertSubtitle"),
        MODAL("modal"),
        TOAST("toast"),
        BADGE("badge"),
        DROPDOWN("dropdown"),
        CARD("card"),
        CHIP("chip"),
        GRID("grid"),
        CUSTOM("custom");

        private final String value;

        public String getValue() {
            return value;
        }

        MoveoOneType(String value) {
            this.value = value;
        }

    }

    public enum MoveoOneAction {
        CLICK("click"),
        VIEW("view"),
        APPEAR("appear"),
        DISAPPEAR("disappear"),
        SWIPE("swipe"),
        SCROLL("scroll"),
        DRAG("drag"),
        DROP("drop"),
        TAP("tap"),
        DOUBLE_TAP("doubleTap"),
        LONG_PRESS("longPress"),
        PINCH("pinch"),
        ZOOM("zoom"),
        ROTATE("rotate"),
        SUBMIT("submit"),
        SELECT("select"),
        DESELECT("deselect"),
        HOVER("hover"),
        FOCUS("focus"),
        BLUR("blur"),
        INPUT("input"),
        VALUE_CHANGE("valueChange"),
        DRAG_START("dragStart"),
        DRAG_END("dragEnd"),
        LOAD("load"),
        UNLOAD("unload"),
        REFRESH("refresh"),
        PLAY("play"),
        PAUSE("pause"),
        STOP("stop"),
        SEEK("seek"),
        ERROR("error"),
        SUCCESS("success"),
        CANCEL("cancel"),
        RETRY("retry"),
        SHARE("share"),
        OPEN("open"),
        CLOSE("close"),
        EXPAND("expand"),
        COLLAPSE("collapse"),
        EDIT("edit"),
        CUSTOM("custom");

        private final String value;

        public String getValue() {
            return value;
        }

        MoveoOneAction(String value) {
            this.value = value;
        }


    }
}
