package com.benjishults.bitnots.tableau

class FolTableau(
        root: FolTableauNode
) : AbstractTableau<FolTableauNode>(
        root) {

    override fun toString(): String {
        return buildString {
            root.preOrderWithPath { n, path ->
                this.append(path.joinToString("."))
                this.append("\n")
                this.append(n.toString())
                //                this.append(n.initialClosers.toString())
                this.append("\n")
                this.append("\n")
                false
            }
        }
    }

}
