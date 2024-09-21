<script setup lang="ts">
import DerUmrechner from '@/components/DerUmrechner.vue';
import ReactiveNonreactiveVue from './components/ReactiveNonreactive.vue';
import StarRating from './components/StarRating.vue';

import { ref } from 'vue';
import DieTabelle from './components/DieTabelle.vue';
import KurzTextReactive from './components/KurzTextReactive.vue';
import KurzTextRef from './components/KurzTextRef.vue';

const teststernzahl = ref(0)
</script>

<template>

  <h1>DerUmrechner</h1>
  <DerUmrechner :faktor="1.5" />
  <p/>
  <DerUmrechner :faktor="42" /> 
  
  <h1>Reactive/Non-Reactive-Beispiel</h1>
  <ReactiveNonreactiveVue/> 

  <hr/>

  <h1>StarRating</h1>
  <StarRating :maxsterne="5" :sterne="3"/>
  <p/>
  <StarRating :maxsterne="3" :sterne="3"/>
  <p/>
  <h4>Per v-model-Two-Way-Binding synchronisierte eigene StarRating-Komponente</h4>
  <StarRating :maxsterne="12" v-model="teststernzahl"/>
  <br>
  Sternanzahl <input type="number" v-model="teststernzahl"> 
  
  <hr/>

  <h1>Tabelle</h1>
  <DieTabelle :von="1" :bis="10"/>
  <p/>

   <h1>KurzTextRef und KurzTextReactive</h1>
  <KurzTextRef :max="10"/>
  <p/>
  <h2>KurzTextReactive.vue</h2>
  <KurzTextReactive :max="10"/>
  <p/> 

</template> 
