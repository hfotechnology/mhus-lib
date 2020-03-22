package de.mhus.lib.core.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.cfg.CfgString;

@SuppressWarnings("deprecation")
public class DefaultShiroSecurity extends MLog implements ShiroSecurity {

    public static CfgString CFG_CONFIG_FILE = new CfgString(ShiroSecurity.class,"iniResourcePath", MApi.getFile(MApi.SCOPE.ETC, "shiro.ini").getPath() );
    private SecurityManager securityManager;
    
    public DefaultShiroSecurity() {
        initialize();
    }
    
    protected void initialize() {
        log().d("Initialize Shiro",CFG_CONFIG_FILE);
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(CFG_CONFIG_FILE.value());
        securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }
    
    @Override
    public SecurityManager getSecurityManager() {
        return securityManager;
    }
    
    @Override
    public Subject createSubject() {
        return new Subject.Builder().buildSubject();
    }

    @Override
    public void updateSessionLastAccessTime() {
        Subject subject = SecurityUtils.getSubject();
        //Subject should never _ever_ be null, but just in case:
        if (subject != null) {
            Session session = subject.getSession(false);
            if (session != null) {
                try {
                    session.touch();
                } catch (Throwable t) {
                    log().e("session.touch() method invocation has failed.  Unable to update " +
                            "the corresponding session's last access time based on the incoming request.", t);
                }
            }
        }
    }

}
