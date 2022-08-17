package com.patriker.syncnote.domain

import java.net.NetworkInterface
import java.net.SocketException

object ListNets {
    @Throws(SocketException::class)
    @JvmStatic
    fun listif() {
        val nets = NetworkInterface.getNetworkInterfaces()
        nets.iterator().forEach{ netint -> displayInterfaceInformation(netint) }
    }


    @Throws(SocketException::class)
    fun getFirstLocalIP(): String{
        val nets = NetworkInterface.getNetworkInterfaces()

        val firstIf: NetworkInterface? =
                    nets.toList()
                        .filterNot{ it.name.startsWith("lo") || !it.inetAddresses.hasMoreElements() }
                        .firstOrNull()

        val ip = firstIf?.inetAddresses!!.toList()
                    .filterNot{addr -> addr.toString().contains(':')}
                    .firstOrNull()

        ip.toString().let { ipstr ->
            when {
                ipstr == "null" -> return ipstr
                else -> return ipstr.drop(1)
            }
        }
    }

    @Throws(SocketException::class)
    fun displayInterfaceInformation(netint: NetworkInterface) {
        System.out.printf("Display name: %s\n", netint.displayName)
        System.out.printf("Name: %s\n", netint.name)
        val inetAddresses = netint.inetAddresses.iterator()
        inetAddresses.iterator().forEach { inetAdress ->
            println(inetAdress.toString())
        }
        System.out.printf("\n")
    }

}