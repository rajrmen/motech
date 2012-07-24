package org.motechproject.decisiontree.domain;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.model.MotechBaseDataObject;

public class TreeDao extends MotechBaseDataObject{
    Tree tree;

    public TreeDao(Tree tree) {
        this.tree = tree;
    }

    public TreeDao() {
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public String name() {
        return tree.getName();
    }
}
