package com.benjishults.bitnots.util

open class TreeNodeImpl(
        override var parent: TreeNode?,
        override val children: MutableList<TreeNode> = mutableListOf()
) : TreeNode
