package net.intelie.snmp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMP4JHelper {

    static final String IP = "demo.snmplabs.com";
    /////
    static final String OID_IF_BASE = ".1.3.6.1.2.1.2.2.1";
    static final String IF_DESC = ".2";
    static final String IF_MTU = ".4";
    static final String IF_SPEED = ".5";
    static final String IF_LAST_CHANGE = ".9";
    static final String IF_INPUT = ".10";
    static final String IF_OUTPUT = ".16";
    ////
    static final String PORT_INDEX = ".2";
    ///
    static final String snmp_comm = "public";
    static final String snmp_vers = "2";//SnmpConstants.version2c;
    static final String snmp_port = "161";
    static Map<String, Integer> versionMap = new HashMap<>();
    ///
    static final String OID_SYS_BASE = ".1.3.6.1.2.1.1";
    static final String SYS_DESCR = ".1.0";
    static final String SYS_UPTIME = ".3.0";
    static final String SYS_NAME = ".5.0";
    static String[] sysArray = {
        SYS_DESCR,
        SYS_UPTIME,
        SYS_NAME
    };
    static String[] ifArray = {
        IF_DESC,
        IF_MTU,
        IF_SPEED,
        IF_LAST_CHANGE,
        IF_INPUT,
        IF_OUTPUT
    };

    static {
        versionMap.put("1", SnmpConstants.version1);
        versionMap.put("2", SnmpConstants.version2c);
        versionMap.put("3", SnmpConstants.version3);
    }

    public static void main(String[] args) {
        for (String str : sysArray) {
            System.out.println(str + " = " + snmpGet(IP, OID_SYS_BASE + str, snmp_comm, snmp_port, snmp_vers));
        }
        for (String str : ifArray) {
            System.out.println(str + " = " + snmpGet(IP, OID_IF_BASE + str + PORT_INDEX, snmp_comm, snmp_port, snmp_vers));
        }
    }

    public static String snmpGet(String ip, String oid, String comm, String port, String vers) {
        String str = "";
        Snmp snmp = null;
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(comm));
            comtarget.setVersion(versionMap.get(vers));
            comtarget.setAddress(new UdpAddress(ip + "/" + port));
            comtarget.setRetries(1);
            comtarget.setTimeout(500);
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);
            snmp = new Snmp(transport);
            ResponseEvent response = snmp.get(pdu, comtarget);
            if (response != null) {
                if (response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                    PDU pduresponse = response.getResponse();
                    str = pduresponse.getVariableBindings().firstElement().toString();
                    if (str.contains("=")) {
                        int len = str.indexOf("=");
                        str = str.substring(len + 1, str.length());
                    }
                }
            } else {
                System.out.println("TIMEOUT");
            }
            snmp.close();
        } catch (IOException | NullPointerException e) {
            return "";
        } finally {
            try {
                snmp.close();
            } catch (IOException ex) {
                Logger.getLogger(SNMP4JHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return str.trim();
    }

}
