/**
 * 
 */
package com.waylau.snmp4j;

/**
 * 说明：
 *
 * @author <a href="http://www.waylau.com">waylau.com</a> 2015年10月22日 
 */
public class App {

	/**
	 * 
	 */
	public App() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ip = "demo.snmplabs.com"; // = 104.236.166.95
		String community = "public";
		String oidval = ".1.3.6.1.2.1.2.2.1.2.1"; // ifDescr.1
		SnmpData.snmpGet(ip, community, oidval);
		
		//Snmp4jData.getIpOfGateway();
	}

}
