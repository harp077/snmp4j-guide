package com.javacodegeeks.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    
    static String IF_MIB=".1.3.6.1.2.1.2.2.1";

    public static void main(String[] args) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse("udp:demo.snmplabs.com/161")); // supply your own IP and port
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);

        Map<String, String> result = doWalk(IF_MIB, target); // ifTable, mib-2 interfaces

        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (entry.getKey().startsWith(IF_MIB+".1.")) {
                System.out.println("ifIndex" + entry.getKey().replace(IF_MIB+".1", "") + ": " + entry.getValue());
            }            
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.2.")) {
                System.out.println("ifDescr" + entry.getKey().replace(IF_MIB+".2", "") + ": " + entry.getValue());
            }
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.3.")) {
                System.out.println("ifType"  + entry.getKey().replace(IF_MIB+".3", "") + ": " + entry.getValue());
            }
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.4.")) {
                System.out.println("ifMtu"   + entry.getKey().replace(IF_MIB+".4", "") + ": " + entry.getValue());
            }   
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.5.")) {
                System.out.println("ifSpeed" + entry.getKey().replace(IF_MIB+".5", "") + ": " + entry.getValue());
            }  
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.9.")) {
                System.out.println("ifLastChange" + entry.getKey().replace(IF_MIB+".9", "") + ": " + entry.getValue());
            } 
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.10.")) {
                System.out.println("ifInOctets" + entry.getKey().replace(IF_MIB+".10", "") + ": " + entry.getValue());
            }  
            if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.16.")) {
                System.out.println("ifOutOctets" + entry.getKey().replace(IF_MIB+".16", "") + ": " + entry.getValue());
            }             
        }
        System.out.println("\n"+result);
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
