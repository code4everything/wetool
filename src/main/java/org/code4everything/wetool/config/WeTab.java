package org.code4everything.wetool.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.code4everything.boot.base.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author pantao
 * @since 2019/7/3
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeTab implements BaseBean, Serializable {

    private static final long serialVersionUID = -7095575648923571810L;

    private List<String> loads;

    private List<String> supports;
}
