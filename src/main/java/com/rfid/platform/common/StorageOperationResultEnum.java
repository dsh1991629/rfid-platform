package com.rfid.platform.common;

public enum StorageOperationResultEnum {

    NOTICE_NOT_EXIST("5000", "入库单不存在"),
    DETAIL_NOT_EXIST("5001", "盘点明细数据为空"),
    SKU_NOT_CONTAINED_BY_IN_NOTICE("5002", "SKU与入库单不一致"),
    EPC_CODE_MATCHED_RULE("5003", "EPC编码不在库中，编码格式正确，是否手动创建"),
    EPC_CODE_NOT_MATCHED_RULE("5003", "EPC编码不在库中，编码格式不正确"),
    SKU_EPC_BIND_NOT_MATCH("5004", "SKU和EPC的绑定关系不正确"),
    EPC_STATE_ABNORMAL("5005", "EPC状态不正确"),




    ;


    private String code;
    private String message;

    StorageOperationResultEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
