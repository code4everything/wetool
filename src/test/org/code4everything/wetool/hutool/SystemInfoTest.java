package org.code4everything.wetool.hutool;

import cn.hutool.system.SystemUtil;
import org.junit.Test;

/**
 * @author pantao
 * @since 2019/8/26
 */
public class SystemInfoTest {

    @Test
    public void test() {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaVirtualMachineSpecification信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJvmSpecInfo());

        builder.append("\r\nJavaVirtualMachineImplementation信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJvmInfo());

        builder.append("\r\nJavaSpecification信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJavaSpecInfo());

        builder.append("\r\nJavaImplementation信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJavaInfo());

        builder.append("\r\nJava运行时信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJavaRuntimeInfo());

        builder.append("\r\n系统信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getOsInfo());

        builder.append("\r\n用户信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getUserInfo());

        builder.append("\r\n当前主机网络地址信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getHostInfo());

        builder.append("\r\n运行时信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getRuntimeInfo());

        System.out.println(builder.toString());
    }
}
