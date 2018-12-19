package com.javacodegeeks.example;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SnmpWalk {
    
    static String IF_MIB=".1.3.6.1.2.1.2.2.1.2";  // ifDescr
    static Set<String> ifset=new HashSet<>();
    static String IP="demo.snmplabs.com";
    static String[] keyArray;
    static Map<String, String> result;
    static String snmp_comm="public";
    static int snmp_vers=SnmpConstants.version2c;
    static String snmp_port="161";    
    

    public static void main(String[] args) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(snmp_comm));
        target.setAddress(GenericAddress.parse("udp:"+IP+"/"+snmp_port)); // supply your own IP and snmp_port
        target.setRetries(1);
        target.setTimeout(1000);
        target.setVersion(snmp_vers);
        result = doWalk(IF_MIB, target); // ifTable, mib-2 interfaces
        for (Map.Entry<String, String> entry : result.entrySet()) {
            keyArray=entry.getKey().replace(".","-").split("-"); // entry.getKey() = .1.3.6.1.2.1.2.2.1.2.*
            ifset.add(keyArray[keyArray.length-1]+":"+entry.getValue());
            //System.out.println(" ifDescr: " + entry.getKey() + " = " + entry.getValue());
            //System.out.println("\n "+keyArray[keyArray.length-1]); 
        }
        System.out.println("\n Walk Map = "+result);
        System.out.println("\n Ports set = "+ifset);
    }

    public static Map<String, String> doWalk(String tableOid, Target target) throws IOException {
        Map<String, String> result = new TreeMap<>();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
        if (events == null || events.size() == 0) {
            System.out.println("Error: Unable to read table...");
            return result;
        }
        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
                continue;
            }
            VariableBinding[] varBindings = event.getVariableBindings();
            if (varBindings == null || varBindings.length == 0) {
                continue;
            }
            for (VariableBinding varBinding : varBindings) {
                if (varBinding == null) {
                    continue;
                }
                result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
            }
        }
        snmp.close();
        return result;
    }

}
