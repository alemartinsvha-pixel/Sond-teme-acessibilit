# SoundTheme Accessibility

Starter Android nativo em Kotlin com Jetpack Compose, serviço de acessibilidade e reprodução de efeitos usando `SoundPool`.

## Compilar

Abra esta pasta no Android Studio e sincronize o Gradle. Para compilar localmente:

```shell
gradle :app:assembleDebug
```

O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`. O workflow do GitHub Actions compila o mesmo APK em cada push e disponibiliza-o como artefato da execução.

## Ativar

Instale o APK, abra o app e toque em **Ativar serviço de acessibilidade**. Na tela de configurações do Android, selecione **SoundTheme Accessibility** e habilite o serviço.

## Pacotes de áudio

Os 20 arquivos WAV em `app/src/main/res/raw/` são efeitos demonstrativos gerados para este starter. Para usar seus próprios sons, substitua-os mantendo os nomes:

O app apresenta os temas como opções de seleção única e salva a escolha. Apenas o tema selecionado é usado pelo serviço durante a navegação; os outros não são reproduzidos em paralelo.

| Tema | Prefixo |
|---|---|
| Padrão | `default` |
| Profético | `prophetic` |
| Suave | `soft` |
| Piano | `piano` |
| Pads | `pads` |

Cada prefixo requer os sufixos `focus`, `click`, `scroll` e `window`, por exemplo `default_focus.wav`. Os sons são carregados uma vez quando o serviço conecta; a escolha do tema é consultada durante a navegação.

O serviço observa apenas os tipos de evento configurados e não solicita acesso ao conteúdo da janela. Siga as políticas do Google Play para serviços de acessibilidade e explique claramente ao usuário a finalidade do serviço.
