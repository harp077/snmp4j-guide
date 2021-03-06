package com.javacodegeeks.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
    
    static String LS_IF_DESCR=".1.3.6.1.2.1.2.2.1.2";     // ifDescr
    static String HS_IF_NANE =".1.3.6.1.2.1.31.1.1.1.1";  // ifName
    static Set<String> ifset=new HashSet<>();
    static String IP="demo.snmplabs.com";
    static List<String> keyArray;
    static Map<String, String> walkMap;
    static Map<String, Integer> versionMap=new HashMap<>();
    static String marked_ports;
    static String snmp_comm="public";
    static String snmp_vers="2";//SnmpConstants.version2c;
    static String snmp_port="161";   
    
    static {
       versionMap.put("1", SnmpConstants.version1);
       versionMap.put("2", SnmpConstants.version2c);
       versionMap.put("3", SnmpConstants.version3);
    }
    

    public static void main(String[] args) throws Exception {
        walkMap = walkSNMP(IP, HS_IF_NANE, snmp_comm, snmp_port, snmp_vers); // ifTable, mib-2 interfaces
        keyArray = walkMap.keySet().stream().map(x->x.replace(".","-").split("-")[12]).collect(Collectors.toList());
        ifset  = walkMap.entrySet().stream().map(x->x.getKey().replace(".","-").split("-")[12]+":"+x.getValue()).collect(Collectors.toSet());
        marked_ports=keyArray.stream().map(x->x+"-").reduce((left, right) -> left + right).get();//.collect(Collectors.toList()).toString();
        //for (Map.Entry<String, String> entry : walkMap.entrySet()) {
            //keyArray=entry.getKey().replace(".","-").split("-"); // entry.getKey() = .1.3.6.1.2.1.2.2.1.2.*
            //ifset.add(keyArray[keyArray.length-1]+":"+entry.getValue());
            //marked_ports=marked_ports+keyArray[keyArray.length-1] +"-";
            //System.out.println(" ifDescr: " + entry.getKey() + " = " + entry.getValue());
            //System.out.println("\n "+keyArray[keyArray.length-1]); 
        //}
        System.out.println("\n Walk Map = "+walkMap);
        System.out.println("\n Ports Set = "+ifset);
        System.out.println("\n Last Port Index  = " + ifset.toArray()[ifset.size()-1].toString().split(":")[0]);        
        System.out.println("\n Version Map = "+versionMap);
        System.out.println("\n marked_ports = "+marked_ports);
        System.out.println("\n keyArray = "+keyArray);
    }

    public static Map<String, String> walkSNMP(String ip, String oid, String comm, String port, String vers) throws IOException {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(comm));
        target.setAddress(GenericAddress.parse("udp:"+ip+"/"+port)); // supply your own IP and snmp_port
        target.setRetries(2);
        target.setTimeout(1000);
        target.setVersion(versionMap.get(vers));        
        Map<String, String> result = new TreeMap<>();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(oid));
        if (events == null || events.size() == 0) {
            System.out.println("Error: Unable to read table...");
            return result;
        }
        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                System.out.println("Error: table OID [" + oid + "] " + event.getErrorMessage());
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
