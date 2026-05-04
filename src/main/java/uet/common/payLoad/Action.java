package uet.common.payLoad;

public enum Action {
    // Xác thực
    LOGIN,
    LOGOUT,
    REGISTER,

    // Sản phẩm đấu giá
    GET_ALL_ITEMS,
    GET_ITEM_ByID,

    // phiên đấu giá
    CREATE_AUCTION,
    GET_ALL_AUCTIONS,
    GET_AUCTION_BY_ID,
    GET_ACTIVE_AUCTIONS,
    CLOSE_AUCTION,

    //  Đặt giá
    PLACE_BID,
    GET_BID_HISTORY,

    // Thông báo
    NEW_BID_UPDATE,
    AUCTION_CLOSED,

    // hệ thống
    ERROR,
    UNKNOWN
}
