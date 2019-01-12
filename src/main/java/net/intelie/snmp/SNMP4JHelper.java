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
    /////  LOW-SPEED
    static final String LS_IF_MIB = ".1.3.6.1.2.1.2.2.1";
    static final String LS_IF_DESC = ".2";
    static final String LS_IF_MTU = ".4";
    static final String LS_IF_SPEED = ".5";
    static final String LS_IF_LAST_CHANGE = ".9";
    static final String LS_IF_INPUT = ".10";
    static final String LS_IF_OUTPUT = ".16";
    /////  HIGH SPEED
    static final String HS_IF_MIB = ".1.3.6.1.2.1.31.1.1.1";
    static final String HS_IF_NAME = ".1";
    static final String HS_IF_INPUT  = ".6";
    static final String HS_IF_OUTPUT = ".10";    
    ///
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
    static String[] LS_ifArray = {
        LS_IF_DESC,
        LS_IF_MTU,
        LS_IF_SPEED,
        LS_IF_LAST_CHANGE,
        LS_IF_INPUT,
        LS_IF_OUTPUT
    };
    static String[] HS_ifArray = {
        HS_IF_NAME,
        HS_IF_INPUT,
        HS_IF_OUTPUT
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
        System.out.println("_______LOW-SPEED IF-MIB:");
        for (String str : LS_ifArray) {
            System.out.println(LS_IF_MIB + str + PORT_INDEX + " = " + snmpGet(IP, LS_IF_MIB + str + PORT_INDEX, snmp_comm, snmp_port, snmp_vers));
        }
        System.out.println("_______HIGH-SPEED IF-MIB:");
        for (String str : HS_ifArray) {
            System.out.println(HS_IF_MIB + str + PORT_INDEX + " = " + snmpGet(IP, HS_IF_MIB + str + PORT_INDEX, snmp_comm, snmp_port, snmp_vers));
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
            comtarget.setTimeout(150);
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
