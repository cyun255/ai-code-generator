package cn.rescld.aicodegeneratebackend.constant;

import java.io.File;

public interface AppConstant {
    /**
     * 代码生成的根目录
     */
    String CODE_GEN_ROOT =
            System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "output";

    /**
     * 代码部署的根目录
     */
    String DEPLOY_ROOT =
            System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "deploy";

    /**
     * 代码部署的域名
     */
    String DEPLOY_DOMAIN = "http://localhost";
}
