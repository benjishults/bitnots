package com.benjishults.bitnots.regression.app

// class ProblemBrowser(
//     private val uiProperties: Properties,
//     width: Double,
//     height: Double,
//     menuBar: MenuBar = MenuBar(),
//     pane: BorderPane = BorderPane()
// ) : Scene(VBox(menuBar, pane), width, height) {
//
//     private val problemSetName = SimpleStringProperty("No problem set selected")
//
//     private val problemSetFile = SimpleObjectProperty<File>(null)
//
//     private val currentJob = SimpleObjectProperty<Job>(null)
//
//     private val runButton = Button("Run")
//
//     @Volatile
//     private var problemFileSet: ProblemFileSet<*> = ProblemFileSet.EMPTY
//         set(value) {
//             problemSetName.set(value.name)
//             field = value
//         }
//
//     private val table: TableView<ProblemFileSetRow<*, *>>
//
//     init {
//         stylesheets.add("css/ui.css")
//         pane.idProperty().set("root")
//
//         menu(menuBar)
//
//         top(pane)
//
//         table = problemTable()
//         pane.center = buildCenter()
//         configureRunButton()
//         bottom(pane)
//     }
//
//     private fun buildCenter(): BorderPane {
//         val trash = Button(
//             null,
//             GlyphsBuilder
//                 .create(FontAwesomeIconView::class.java)
//                 .glyph(FontAwesomeIcon.TRASH)
//                 .build()
//         ).also { trashButton ->
//             trashButton.setOnAction { _ ->
//                 table.items.removeAll(table.selectionModel.selectedItems)
//             }
//         }
//         val edit = Button(
//             null,
//             GlyphsBuilder.create(FontAwesomeIconView::class.java)
//                 .glyph(FontAwesomeIcon.EDIT)
//                 .build()
//         )
//         return BorderPane(
//             ScrollPane(table),
//             null,
//             VBox(
//                 5.0,
//                 trash,
//                 edit
//             ).also { vBox ->
//                 vBox.alignmentProperty().value = Pos.TOP_CENTER
//                 vBox.borderProperty().value = Border(BorderStroke(null, null, null, BorderWidths(5.0, 5.0, 5.0, 5.0)))
//             },
//             null,
//             null
//         )
//     }
//
//     private fun top(pane: BorderPane) {
//         pane.top = VBox(5.0).also { vbox ->
//             vbox.children.addAll(
//                 FlowPane().also { bottomPane ->
//                     bottomPane.hgap = 5.0
//                     bottomPane.padding = Insets(5.0)
//                     bottomPane.children.addAll(
//                         saveButton(),
//                         Button("Delete Problem Set").also { deleteButton ->
//                             deleteButton.setOnAction { _ ->
//                                 TODO("Implement")
//                             }
//                         }
//                     )
//                 },
//                 Label().also { label ->
//                     label.textProperty().bind(problemSetName)
//                 }
//             )
//         }
//     }
//
//     private fun saveButton() =
//         Button("Save").also { saveButton ->
//             saveButton.setOnAction { _ ->
//                 if (problemSetName.get().willOverwriteExistingProblemSet()) {
//                     Alert(
//                         Alert.AlertType.WARNING,
//                         "This will overwrite an existing problem set configuration and its history.  Are you sure?"
//                     ).showAndWait()
//                         .filter { it === ButtonType.OK }
//                         .ifPresent { problemFileSet.writeNewProblemSet() }
//                 } else {
//                     problemFileSet.writeNewProblemSet()
//                 }
//             }
//         }
//
//     private fun menu(menuBar: MenuBar) {
//         val fileMenu = Menu("File")
//         fileMenu.items.addAll(
//             newProblemSetMenuItem(),
//             openProblemSetMenuItem(),
//             recentProblemSetsMenu(),
//             quitMenuItem()
//         )
//
//         menuBar.menus.addAll(fileMenu, Menu("Help"))
//     }
//
//     private fun bottom(pane: BorderPane) {
//         pane.bottom = FlowPane().also { bottomPane ->
//             bottomPane.hgap = 5.0
//             bottomPane.padding = Insets(5.0)
//             bottomPane.children.addAll(
//                 runButton,
//                 Button("See History").also { historyButton ->
//                     historyButton.setOnAction { _ ->
//                         TODO("Implement")
//                         // loadFile("problem")?.let {
//                         //     // TODO set as current problem
//                         //     loadProblem(it)
//                         // }
//                     }
//                 },
//                 Button("Unlock").also { unlockButton ->
//                     unlockButton.setOnAction { _ ->
//                         TODO("Implement")
//                         // loadFile("theory")?.let {
//                         //     // TODO add to theories loaded
//                         // }
//                     }
//                 })
//         }
//     }
//
//     private fun configureRunButton() =
//         runButton.setOnAction {
//             // TODO this should query for harness configuration
//             // val toParser: (FormulaForm) -> Parser<*, *> = this::toParser
//             if (currentJob.value !== null) {
//                 val fileFetcher = TptpFileFetcher
//                 problemFileSet.problemFiles.forEach { (fileDescriptor) ->
//                     // val onProof: (ProofInProgress) -> Unit = { pip -> updateTableItem(problemRun, pip) }
//                     // val (fileDescriptor, harness) = problemFileDescriptor
//                     fileDescriptor.parser<FolAnnotatedFormula>().parseAndClassify(
//                         TptpFormulaClassifier(),
//                         fileDescriptor as TptpProblemFileDescriptor,
//                         fileFetcher as FileFetcher<*, TptpFormulaForm, FileDescriptor<TptpFormulaForm, *>>
//                     ).let { (hyps, targets) ->
//                         onJobStarted()
//                         currentJob.value = CoroutineScope(Dispatchers.Default).launch {
//                             // TODO extract data for table
//                             // TODO maybe spinny gif in each row when waiting
//                             // harness.proveAllTargets(hyps, targets)
//                         }.also { job ->
//                             job.invokeOnCompletion { e ->
//                                 onJobFinished(e)
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//
//     private fun onJobStarted() {
//         runButton.disableProperty().value = true
//     }
//
//     private fun onJobFinished(e: Throwable?) {
//         e?.printStackTrace()
//         Platform.runLater {
//             runButton.disableProperty().value = false
//         }
//
//     }
//
//     private fun stopButton() =
//         Button("Stop All").also { runButton ->
//             runButton.setOnAction {
//                 // TODO extract this to be testable
//                 // val toParser: (FormulaForm) -> Parser<*, *> = this::toParser
//                 val fileFetcher = TptpFileFetcher
//                 problemFileSet.problemFiles.forEach { (fileDescriptor, harness) ->
//                     // val onProof: (ProofInProgress) -> Unit = { pip -> updateTableItem(problemRun, pip) }
//                     // val (fileDescriptor, harness) = problemFileDescriptor
//                     fileDescriptor.parser<FolAnnotatedFormula>().parseAndClassify(
//                         TptpFormulaClassifier(),
//                         fileDescriptor as TptpProblemFileDescriptor,
//                         fileFetcher as FileFetcher<*, TptpFormulaForm, FileDescriptor<TptpFormulaForm, *>>
//                     ).let { (hyps, targets) ->
//                         // TODO extract data for table
//                         // TODO maybe spinny gif in each row when waiting
//                         CoroutineScope(Dispatchers.Default).launch {
//                             harness.proveAllTargets(hyps, targets)
//                         }
//                     }
//                 }
//             }
//         }
//
//     private fun updateTableItem(
//         problemFile: ProblemFileSetRow<*, *>,
//         proofInProgress: ProofInProgress
//     ) {
//         table.items.set(
//             table.items.indexOf(problemFile),
//             ProblemFileSetRow(
//                 problemFile.fileDescriptor
//                 // problemFile.harness,
//                 // proofInProgress.indicator.toProblemRunStatus()
//             )
//         )
//     }
//
//     private fun problemTable(): TableView<ProblemFileSetRow<*, *>> {
//         val table = TableView<ProblemFileSetRow<*, *>>()
//         val fileNameCol = TableColumn<ProblemFileSetRow<*, *>, String>("File")
//         fileNameCol.setCellValueFactory { column ->
//             ReadOnlyObjectWrapper(column.value.fileDescriptor.toFileName())
//         }
//         val sourceCol = TableColumn<ProblemFileSetRow<*, *>, String>("Source")
//         sourceCol.setCellValueFactory { _ ->
//             ReadOnlyObjectWrapper(TptpFileRepo.abbreviation)
//         }
//         val harnessCol = TableColumn<ProblemFileSetRow<*, *>, Harness<*, *>>("Harness")
//         harnessCol.setCellValueFactory { column ->
//             ReadOnlyObjectWrapper(column.value.harness)
//         }
//         val statusCol = TableColumn<ProblemFileSetRow<*, *>, ProblemRunStatus>("Last Run")
//         statusCol.setCellValueFactory { column ->
//             ReadOnlyObjectWrapper(if (column.value is ProblemFileSetRow) column.value.status else null)
//         }
//         table.columnResizePolicy = Callback { _ ->
//             true
//         }
//         table.selectionModelProperty().value.selectionMode = SelectionMode.MULTIPLE
//         table.columns.addAll(
//             fileNameCol,
//             sourceCol,
//             harnessCol,
//             statusCol
//         )
//         return table
//     }
//
//     private fun recentProblemSetsMenu() = Menu("Recent")
//
//     private fun openProblemSetMenuItem() = MenuItem("Open...").also { open ->
//         open.setOnAction { e: ActionEvent ->
//             FileChooser().also { fileChooser ->
//                 fileChooser.title = "Open problem set"
//                 fileChooser.extensionFilters.addAll(
//                     FileChooser.ExtensionFilter("Yaml Files", "*.yml"),
//                     FileChooser.ExtensionFilter("All Files", "*.*")
//                 )
//                 val file = fileChooser.showOpenDialog(window);
//                 Path.of(file.absolutePath).let { path ->
//                     CoroutineScope(Dispatchers.IO).launch {
//                         // TODO save directory to user properties
//                         // TODO populate
//                             problemFileSet = builder.build()
//                             table.items = FXCollections.observableList(
//                                 problemFileSet.problemFiles
//                             )
//
//
//                     }
//                 }
//             }
//         }
//     }
//
//     private fun newProblemSetMenuItem(): MenuItem {
//         return MenuItem("New").also { new ->
//             new.setOnAction { e: ActionEvent ->
//                 NewProblemSetDialog()
//                     .showAndWait()
//                     .ifPresentOrElse(
//                         { builder ->
//                             // Popup().also { popup ->
//                             //     popup.content.add(Text("Initializing Problem Set '${builder.name}'"))
//                             //     popup.show(this.window)
//                             problemFileSet = builder.buildNew()
//                             table.items = FXCollections.observableList(
//                                 problemFileSet.problemFiles
//                             )
//                             // popup.hide()
//                             // }
//                         }
//                     ) {
//                         // do nothing
//                     }
//             }
//         }
//     }
//
//     private fun quitMenuItem(): MenuItem {
//         return MenuItem("Quit").also { quit ->
//             quit.setOnAction { e: ActionEvent ->
//                 Platform.exit()
//             }
//         }
//     }
//
// }
