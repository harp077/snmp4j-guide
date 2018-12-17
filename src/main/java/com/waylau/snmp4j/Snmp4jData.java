package com.waylau.snmp4j;

import java.io.IOException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Snmp4jData {

    /*public static String getIpOfGateway() {
        String gatewayIpString = null;
        String gatewayIp = null;
        try {
            CommunityTarget localhost = new CommunityTarget();
            Address address = GenericAddress.parse("udp:127.0.0.1/161");
            localhost.setAddress(address);
            localhost.setCommunity(new OctetString("public"));
            localhost.setRetries(2);
            localhost.setTimeout(5 * 60);
            localhost.setVersion(SnmpConstants.version2c);
             // Set protocols of UDP and SNMP
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp protocol = new Snmp(transport);
             // OID binding
            PDU requestPDU = new PDU();
            requestPDU.add(new VariableBinding(new OID("1.3.6.1.2.1.4.21.1.7")));//ipRouteNextHop  
            requestPDU.setType(PDU.GETNEXT);
            /////
            ResponseEvent responseEvent = protocol.send(requestPDU, localhost);
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU != null) {
                VariableBinding getIp = responsePDU.get(0);
                gatewayIpString = getIp.toString();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        gatewayIp = gatewayIpString.substring(31);
        return gatewayIp;

    }*/
}
