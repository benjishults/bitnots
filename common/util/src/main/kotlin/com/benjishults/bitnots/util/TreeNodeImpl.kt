package com.benjishults.bitnots.util

open class TreeNodeImpl<TN: TreeNode<TN>>(
        override var parent: TN?,
        override val children: MutableList<TN> = mutableListOf()
) : TreeNode<TN>
