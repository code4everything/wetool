package org.code4everything.wetool.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.code4everything.boot.base.bean.BaseBean;

import java.io.Serializable;

/**
 * @author pantao
 * @since 2019/7/3
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeInitialize implements BaseBean, Serializable {

    private static final long serialVersionUID = -3706972162680878384L;

    private Integer width;

    private Integer height;

    private Boolean fullscreen;

    private WeTab tabs;
}
