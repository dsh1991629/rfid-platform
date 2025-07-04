package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户删除数据传输对象
 * 用于封装账户删除操作所需的参数
 */
@Data
@Schema(description = "账户删除数据传输对象")
public class AccountDeleteDTO implements Serializable {

    /**
     * 账户ID
     */
    @Schema(description = "账户ID", example = "1", required = true)
    private Long id;

}
