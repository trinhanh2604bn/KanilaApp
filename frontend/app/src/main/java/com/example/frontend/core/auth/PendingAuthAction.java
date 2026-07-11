package com.example.frontend.core.auth;

import android.os.Bundle;

public class PendingAuthAction {
    public enum ActionType {
        NONE,
        OPEN_ACCOUNT,
        ADD_TO_WISHLIST,
        REMOVE_FROM_WISHLIST,
        OPEN_WISHLIST,
        START_CHECKOUT,
        OPEN_ORDER_LIST,
        OPEN_ORDER_DETAIL,
        SAVE_BEAUTY_PROFILE,
        OPEN_VOUCHER_WALLET,
        SAVE_COUPON,
        WRITE_REVIEW,
        CREATE_COMMUNITY_POST,
        CREATE_REELS,
        JOIN_CHALLENGE,
        COMMUNITY_INTERACTION,
        OPEN_LOYALTY,
        OPEN_SUPPORT_TICKET
    }

    private final ActionType actionType;
    private final String sourceScreen;
    private final int returnDestinationId;
    private final Bundle extras;

    public PendingAuthAction(ActionType actionType, String sourceScreen, int returnDestinationId, Bundle extras) {
        this.actionType = actionType;
        this.sourceScreen = sourceScreen;
        this.returnDestinationId = returnDestinationId;
        this.extras = extras;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getSourceScreen() {
        return sourceScreen;
    }

    public int getReturnDestinationId() {
        return returnDestinationId;
    }

    public Bundle getExtras() {
        return extras;
    }
}
