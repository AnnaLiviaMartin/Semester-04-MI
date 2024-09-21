<template>
  <div>
    <div class="block">
      <input type="text" v-model="suchfeld" placeholder="Suchbegriff oder Sterne" class="input" />
    </div>
    <div class="block">
      <table class="table is-hoverable">
        <thead>
          <th style="width:75%">Erkenntnis</th>
          <th style="width:25%">Rating</th>
        </thead>
        <tbody>
          <TabelleZeile :item="ele" v-for="ele in listitems" :key="ele.id" @delete-zeile="delZeile($event)" />
        </tbody>
      </table>
      <button @click="update()" class="button btn">Liste erfrischen</button>
    </div>
    <div class="block">
      <div class="notification is-danger" v-show="neuigkeiten">{{ neuigkeiten }}</div>
    </div>
  </div>
</template>

<!-- ---------------------------------------------------- -->

<script setup lang="ts">
/*****************************************
 * Diese Komponente dient als Demo für Vue-Events (lokal und globaler EventBus).
 * Die Events kommen aus den eingebetteten <Zeilen> und deren <StarRating> Komponenten.
 * 'delete-zeile' von Zeile an Tabelle (lokales Event)
 * 'star-rating-changed' von eingebettettem StarRating an Zeile
 * 'event-spruch' von Zeile (wenn Textteil angeklickt oder star-rating-changed empfangen) per EventBus
 *     an App-Komponente mit Jumbotron (zeigt Spruch mit zuletzt geändertem Rating an; 'EventBus'-Demo)
 *
 * Zudem Beispiel für direktes REST-Laden aus Vue-Komponente (ohne Vuex)
 *****************************************/

import {
  onMounted,
  ref,
  computed,
  watch,
} from "vue";

import type { TabItem } from '@/components/TabItem'
import TabelleZeile from "@/components/DieTabelleZeile.vue";

const props = defineProps<{
  von: number,
  bis: number
}>()

const neuigkeiten = ref("");
const suchfeld = ref("");

const items = ref<TabItem[]>([]);

const listitems = computed(() => {
  const n: number = suchfeld.value.length;
  if (suchfeld.value === "*".repeat(n)) {
    return items.value.filter(e => e.stars >= n);
  }
  if (suchfeld.value.length < 3) {
    return items.value;
  } else {
    return items.value.filter(e =>
      e.text.toLowerCase().includes(suchfeld.value.toLowerCase())
    );
  }
});

/*
 % curl "http://numbersapi.com/1..5?json"                                                                                                                                                                                                                                                 webauswert.23686/knguy001 krassus
{
"1": "1 is the number of Gods in monotheism.",
"2": "2 is the number of stars in a binary star system (a stellar system consisting of two stars orbiting around their center of mass).",
"3": "3 is the number of words or phrases in a Tripartite motto.",
"4": "4 is the number of completed, numbered symphonies by Johannes Brahms.",
"5": "5 is the number of interlocked rings in the symbol of the Olympic Games, representing the number of inhabited continents represented by the Olympians (counting North America and South America as one continent)."
}
*
* NUR ZU DEMO-ZWECKEN im <script> einer Vue-Komponente - solche
* Logik lagert man normalerweise in ein separates Modul aus (kommt später)
*/
async function update() {
  neuigkeiten.value = "";
  try {
    const url = `http://numbersapi.com/${props.von}..${props.bis}?json`;
    //const response = await fetch(url, { mode: "cors" });
    const response = await fetch(url);
    const data = await response.json();
    const lst: TabItem[] = [];
    for (const i in data) {
      const nr = parseInt(i);
      lst.push({ id: nr, text: data[i], stars: nr % 5 });
    }
    items.value = lst;
  } catch (exc) {
    console.error("update: fetch exception " + exc);
    neuigkeiten.value = "fetch catch: " + exc;
  }
}

function delZeile(id: number): void {
  neuigkeiten.value = "Löschwunsch für " + id;
  items.value = items.value.filter(ele => ele.id !== id);
}

onMounted(async () => {
  /* Initialisierung der Sprüche-Liste, sobald Komponente geladen wird */
  update();
});

watch(suchfeld, (neu, alt) => {
  console.log(`Suchfeld geändert von ${alt} zu ${neu}`);
});

</script>

<!-- ---------------------------------------------------- -->

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
