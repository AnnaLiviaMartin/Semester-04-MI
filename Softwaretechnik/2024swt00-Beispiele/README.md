# Ampel

Dieses JavaFX-Projekt soll eine manuell schaltbare Ampel realisieren.

  * Das GUI ist bereits im Unterpaket "gui" vorbereitet und soll nicht verändert werden.
  * Die Ampel-**Logik** ist selbst im Unterpaket "anwendungslogik", Klasse AmpelModel.java, zu implementieren
    - Parameter, Namen und Rückgabetypen der vorgegebenen Methoden reset(), tick() und isRot(), isGelb() und isGruen()
      dürfen nicht verändert werden
    - Beim Schalten der Ampel sollen Veränderungen der Lampen per PropertyChangeEvent an die GUI 
      signalisiert werden, damit diese sich korrekt anpasst.
    - Das AmpelModel darf nicht direkt auf das GUI (insb. nicht auf AmpelComponent) zugreifen
      (AmpelComponent kennt AmpelModel, aber nicht umgekehrt),
    - es dürfen nur PropertyChange-Events für tatsächliche Lampenzustandsänderungen geschickt werden
      (also keine überflüssigen Events, wenn sich nichts geändert hat).
  * Die JavaFX-FXML-Datei liegt in src/main/resources. Sie darf **nicht** unter src/main/java mit
    dem Programmcode vermischt abgelegt werden, weil dann nicht sichergestellt ist, dass sie
    bei Ausführung des Programms zusammen mit dem compilierten Java-Code bereitgestellt wird.
  * im src/test/... Zweig ist ein AmpelModelTest.java vorbereitet, hier sollen Unittest-Methoden
    zur Überprüfung des korrekten Ampelverhaltens ausimplementiert werden
  * Das Projekt ist mit "./gradlew test" testbar und mit "./gradlew run" ausführbar
    (GUI erscheint und Ampel ist über die beiden enthaltenen Buttons schaltbar)
