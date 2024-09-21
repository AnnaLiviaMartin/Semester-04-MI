import { ref, readonly } from 'vue';

const info = ref<string>("");

export function useInfo() {
  function loescheInfo() {
    info.value = "";
  }

  function setzeInfo(msg: string) {
    info.value = msg;
  }

  return {
    info: readonly(info),
    loescheInfo,
    setzeInfo
  };
}
