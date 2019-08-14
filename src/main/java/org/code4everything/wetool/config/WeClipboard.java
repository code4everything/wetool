package org.code4everything.wetool.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.code4everything.boot.base.bean.BaseBean;

import java.io.Serializable;

/**
 * @author pantao
 * @since 2019/8/14
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeClipboard implements BaseBean, Serializable {

    private static final long serialVersionUID = 7015684964004144782L;

    /**
     * 剪贴板历史的本地记录长度
     */
    private Integer localSize;

    /**
     * 推送接口，方法POST，数据格式{timestamp:0,clipboard:"the latest clipboard",type:"str/img"}，图片采用BASE64编码
     */
    private String pushApi;

    /**
     * 拉取接口，方法GET，数据格式{data:{clipboard:"recently clipboard",type:"img/str"},otherProperty:"not important for this
     * app"}，图片采用BASE64编码
     */
    private String pullApi;

    /**
     * 是否开启剪贴板同步
     */
    private Boolean enableSync;
}
