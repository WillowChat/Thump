package engineer.carrot.warren.thump.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {
    public String server = "";
    public int port = 6697;
    public String nickname = "";

    public boolean identifyWithNickServ = false;
    public String nickServPassword = "";

    public List<String> autoJoinChannels = new ArrayList<String>();

    public boolean forceAcceptCertificates = false;
    public Set<String> forciblyAcceptedCertificates = new HashSet<String>();
}
