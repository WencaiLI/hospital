package com.thtf.environment.config;

import com.thtf.common.dto.itemserver.ItemTypeAndParameterTypeCodeDTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/5 17:53
 * @Description:
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "environment")
public class ParameterConfigNacos {
    private List<ItemTypeAndParameterTypeCodeDTO> itemTypeAndParameterTypeCodeList;
}
