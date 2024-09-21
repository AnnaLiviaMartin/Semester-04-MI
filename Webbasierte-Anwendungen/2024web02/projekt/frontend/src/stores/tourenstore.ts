import { computed, onBeforeUnmount, reactive, readonly } from 'vue'
import {defineStore} from 'pinia'
import type {ITourDTD} from '@/stores/ITourDTD'
import {useInfo} from '@/composables/useInfo'

import type {IFrontendNachrichtEvent} from '@/services/IFrontendNachrichtEvent'
import {Client} from '@stomp/stompjs'

const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/tour'

let stompClientInstance: Client | null = null

export const useTourenStore = defineStore('tourenstore', () => {
    const tourdata = reactive({
        ok: false,
        lst: [] as ITourDTD[]
    })    //repräsentiert von Store verwaltete State

    async function updateTourListe(): Promise<void> {
        let responseMessage = '';
        try {
            const response = await fetch('/api/tour')   //KEIN LOCALHOST! Nur relative Pfade nutzen!
            if (!response.ok) {
                responseMessage = response.statusText
                throw new Error(responseMessage)
            }
            tourdata.lst = await response.json()
            tourdata.ok = true
            startTourLiveUpdate() // Aktivierung der Live-Updates
        } catch (error) {
            tourdata.lst = []     // Tourenliste auf leeres Array gesetzt
            tourdata.ok = false   // ok-Property auf false
            useInfo().setzeInfo('Response: ' + responseMessage.toString())
        }
    }

    function startTourLiveUpdate() {
        if (!stompClientInstance) {
            stompClientInstance = new Client({brokerURL: wsurl})

            stompClientInstance.onConnect = (frame) => {
                // Callback: erfolgreicher Verbindungsaufbau zu Broker
                console.log('Connected to STOMP server')
                if (stompClientInstance == null)
                    throw new Error("Stomp client connection failed")

                stompClientInstance.subscribe(DEST, async (message) => {
                    // Callback: Nachricht auf DEST empfangen empfangene Nutzdaten in message.body abrufbar,
                    // ggf. mit JSON.parse(message.body) zu JS konvertieren
                    const parsedMessage = JSON.parse(message.body) as IFrontendNachrichtEvent
                    console.log(parsedMessage)

                    await updateTourListe()
                })
            }

            stompClientInstance.onDisconnect = () => {
                console.log('Disconnected from STOMP server')
            }

            // Verbindung zum Broker aufbauen
            stompClientInstance.activate()
        }
    }

    onBeforeUnmount(() => {
        if (stompClientInstance) {
            stompClientInstance.deactivate()
            stompClientInstance = null
        }
    })

    return {
        liste: computed(() => tourdata.lst),
        okbool: computed(() => tourdata.ok),
        tourdata: readonly(tourdata),
        updateTourListe
    }

})