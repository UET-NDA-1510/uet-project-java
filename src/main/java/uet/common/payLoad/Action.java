package uet.common.payLoad;

public enum Action {
    // Xác thực
    LOGIN,
    LOGOUT,
    REGISTER,
    // người dùng
    GET_ALL_USER,
    DELETE_USER,
    // Sản phẩm đấu giá
    CREATE_ITEM,
    EDIT_ITEM,
    GET_ITEM_PENDING,
    GET_ALL_ITEMS,
    GET_ITEM_ByID,
    DELETE_ITEM,
    // phiên đấu giá
    AUCTION_EXTENDED,
    CREATE_AUCTION,
    GET_ALL_AUCTIONS,
    GET_AUCTION_BY_ID,
    GET_INFO_AUCTION_BY_ID,
    GET_ACTIVE_AUCTIONS,
    AUCTION_STARTED,
    AUCTION_ENDED,
    CLOSE_AUCTION,
    Line_Chart,
    //  Đặt giá
    PLACE_BID,
    GET_BID_HISTORY,
    GET_NOTIFI_BID,

    // Thông báo
    NEW_BID_UPDATE,
    AUCTION_CLOSED,

    // hệ thống
    ERROR,
    SUCCESSS
}
