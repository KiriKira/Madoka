package com.callagent.host.data

import android.content.Context
import com.callagent.host.mesh.HostNodes
import com.callagent.rtcmedia.*

/** Registers paired sessions locally; credentials are fetched only when preparing a call. */
internal object RelayNodeSources {
    fun configure(context:Context,currentInstance:String?=null):String? {
        val active=SessionStore(context).read()
        val entries=HostNodes.nodes(context).mapNotNull{node->HostNodes.sessionFor(context,node)?.let{it to HostNodes.storeFor(context,node)}}
            .ifEmpty{active?.let{listOf(it to SessionStore(context))}.orEmpty()}
        RelayRepository.retainNodes(entries.map{"node-"+it.first.deviceId}.toSet())
        var current:String?=null
        for((session,store) in entries){
            val id="node-"+session.deviceId
            val name="节点 "+HostNodes.hostLabel(session.apiBaseUrl)
            if(session.sessionInstanceId==(currentInstance?:active?.sessionInstanceId))current=id
            if(NodeControlSocket.supports(session.apiBaseUrl,"ice_servers_v1"))RelayRepository.observeNode(context,id,name)
            val client=ApiClient(session.apiBaseUrl,store,600,600)
            RelayRepository.registerNode(id,name,session.sessionInstanceId){
                try{client.relayCredentials()}catch(e:ApiFailure){throw MediaApiException(e.httpStatus,e.code)}catch(_:SessionNeedsPairing){throw MediaApiException(401,"SESSION_REVOKED")}
            }
        }
        return current
    }
}
