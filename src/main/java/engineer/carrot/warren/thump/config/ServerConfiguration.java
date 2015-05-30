package engineer.carrot.warren.thump.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraftforge.common.config.Configuration;

import java.util.HashSet;
import java.util.Set;

public class ServerConfiguration {
    public String ID = "";

    public String server = "";
    private static final String SERVER_KEY = "Server";

    public int port = 6697;
    private static final String PORT_KEY = "Port";
    private static final int PORT_MIN = 1;
    private static final int PORT_MAX = 65535;

    public String nickname = "thump-server";
    private static final String NICKNAME_KEY = "Nickname";

    public Set<String> channels = new HashSet<String>();
    private static final String CHANNELS_KEY = "Channels";

    // Nickserv

    public boolean identifyWithNickServ = false;
    private static final String IDENTIFY_WITH_NICKSERV_KEY = "IdentifyWithNickserv";

    public String nickServPassword = "";
    private static final String NICKSERV_PASSWORD_KEY = "NickservPassword";

    // TLS

    public boolean useTLS = true;
    private static final String USE_TLS_KEY = "UseTLS";

    public boolean forceAcceptCertificates = false;
    private static final String FORCE_ACCEPT_CERTIFICATES_KEY = "ForceAcceptCertificates";

    public Set<String> forciblyAcceptedCertificates = new HashSet<String>();
    private static final String FORCIBLY_ACCEPTED_CERTIFICATES_KEY = "ForciblyAcceptedCertificates";

    public ServerConfiguration(String category, Configuration configuration) {
        this.ID = category;

        configuration.setCategoryPropertyOrder(category, Lists.newArrayList(SERVER_KEY, PORT_KEY, NICKNAME_KEY, CHANNELS_KEY));
        this.server = configuration.getString(SERVER_KEY, category, this.server, "");
        this.port = configuration.getInt(PORT_KEY, category, this.port, PORT_MIN, PORT_MAX, "");
        this.nickname = configuration.getString(NICKNAME_KEY, category, this.nickname, "");
        String[] channels = configuration.getStringList(CHANNELS_KEY, category, new String[]{""}, "");
        this.channels = Sets.newHashSet(channels);

        String nickservCategory = category + ".nickserv";
        configuration.setCategoryPropertyOrder(nickservCategory, Lists.newArrayList(IDENTIFY_WITH_NICKSERV_KEY, NICKSERV_PASSWORD_KEY));
        this.identifyWithNickServ = configuration.getBoolean(IDENTIFY_WITH_NICKSERV_KEY, nickservCategory, this.identifyWithNickServ, "");
        this.nickServPassword = configuration.getString(NICKSERV_PASSWORD_KEY, nickservCategory, this.nickServPassword, "");

        String tlsCategory = category + ".tls";
        configuration.setCategoryPropertyOrder(tlsCategory, Lists.newArrayList(USE_TLS_KEY, FORCE_ACCEPT_CERTIFICATES_KEY, FORCIBLY_ACCEPTED_CERTIFICATES_KEY));
        this.useTLS = configuration.getBoolean(USE_TLS_KEY, tlsCategory, this.useTLS, "");
        this.forceAcceptCertificates = configuration.getBoolean(FORCE_ACCEPT_CERTIFICATES_KEY, tlsCategory, this.forceAcceptCertificates, "");
        String[] forciblyAcceptedCertificates = configuration.getStringList(FORCIBLY_ACCEPTED_CERTIFICATES_KEY, tlsCategory, new String[]{""}, "");
        this.forciblyAcceptedCertificates = Sets.newHashSet(forciblyAcceptedCertificates);
    }
}
