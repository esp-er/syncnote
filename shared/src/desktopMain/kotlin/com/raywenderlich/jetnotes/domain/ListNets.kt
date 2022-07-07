package com.raywenderlich.jetnotes.domain

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

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

        val firstIf: NetworkInterface =
                    nets.toList().filterNot{ it.name.startsWith("lo") }
                        .first()

        val ip = firstIf.inetAddresses.toList()
                    .filterNot{addr -> addr.toString().contains(':')}
                    .first()

        return ip.toString().drop(1)
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