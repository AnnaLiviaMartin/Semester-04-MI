<template>
  <header>
    <nav id="header-navigation">
      <ul id="user">
        <li id="icon"></li>
        <li><RouterLink to="/touren" class="buttons">Touren</RouterLink></li>
      </ul>
      <ul id="language-buttons">
        <li class="single-language-button"><a class="lang-button">DE</a></li>
        <li class="single-language-button"><a class="lang-button">NL</a></li>
        <li class="single-language-button"><a class="lang-button">EN</a></li>
        <li class="single-language-button"><a class="lang-button">IT</a></li>
      </ul>
    </nav>
  </header>

  <div v-if="info !== ''">
    <div id="error-head">
      <p>ALARM!!!</p>
      <button id="close-button" @click="loescheInfo">X</button>
    </div>
    <div id="error-body">
      {{ info }}
    </div>
  </div>

  <!--Todo: Wie wird man über RouterView zur TourView geleitet????-->
  <RouterView/>

  <footer>
    <nav id="footer-navigation">
      <ul id="footer-buttons">
        <li class="buttons footer-button"><a>Impressum</a></li>
        <li class="buttons footer-button"><a>Kontakt</a></li>
      </ul>
    </nav>
  </footer>
</template>

<script setup lang="ts">
import { useInfo } from '@/composables/useInfo'

const { info, loescheInfo, setzeInfo } = useInfo()
setzeInfo("Drück mich hier mit deiner Nase!")

</script>

<style scoped>
</style>
