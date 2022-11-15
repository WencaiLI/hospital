package com.thtf.face_recognition.dto.mapstruct;

import com.github.pagehelper.PageInfo;
import com.thtf.face_recognition.vo.PageInfoVO;
import org.mapstruct.Mapper;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 10:55
 * @Description: PageHelper 中 pageInfo DTO
 */
@Mapper(componentModel = "spring")
public interface PageInfoConvert {
    /**
     * @Author: liwencai
     * @Description: 将 pageHelper 中 pageInfo 转换为 PageInfoVO
     * @Date: 2022/9/23
     * @Param pageInfo:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    PageInfoVO toPageInfoVO(PageInfo pageInfo);
}
