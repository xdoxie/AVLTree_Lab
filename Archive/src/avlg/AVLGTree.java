package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author YOUR NAME HERE!
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private boolean found;
	private int imbalance;
	private AVLNode root;
	private int size;
	
	protected class AVLNode {
		AVLNode lChild;
		AVLNode rChild;
		protected T data;
		protected int height;
		
		public AVLNode(T element){
			data = element;
			height=0;
			lChild = rChild = null;
		}
		
		public AVLNode rightRotate(AVLNode x) {
			AVLNode y= x.lChild;
			AVLNode TreeB= y.rChild;
			
			y.rChild=x;
			x.lChild=TreeB;
			
			x.height=Math.max(height(x.lChild), height(x.rChild))+1;
			y.height=Math.max(height(y.lChild), height(y.rChild))+1;
			return y;
		}
		public AVLNode leftRotate(AVLNode y) {
			AVLNode x= y.rChild;
			AVLNode TreeB= x.lChild;
			
			x.lChild=y;
			y.rChild=TreeB;
			
			y.height=Math.max(height(y.lChild), height(y.rChild))+1;
			x.height=Math.max(height(x.lChild), height(x.rChild))+1;
			
			return x;
		}
		public AVLNode leftRightRotate(AVLNode target) {
			target.lChild=leftRotate(target.lChild);
			target= rightRotate(target);
			return target;
		}
		public AVLNode rightLeftRotate(AVLNode target) {
			target.rChild=rightRotate(target.rChild);
			target= leftRotate(target);
			return target;
		}
		public T search (T key) {
			if(key.compareTo(data)==0) {
				return data;
			}else if (key.compareTo(data)<0&&lChild!=null) {
				return lChild.search(key);
			}else if (key.compareTo(data)>0 &&rChild!=null) {
				return rChild.search(key);
			}else
				return null;
		}
		public AVLNode insert (AVLNode node,T key) {
			if (node==null) {
				return (new AVLNode(key));
			}
			if (key.compareTo(node.data)<0) {
					node.lChild=insert(node.lChild,key);
					if (getBalance(node)>imbalance) {
						if(key.compareTo(node.lChild.data)<0) {
							node= rightRotate(node);
						}else
							node=leftRightRotate(node);
						}				
			}else {
				node.rChild= insert(node.rChild,key);
				if(getBalance(node)<-imbalance) {
					if(key.compareTo(node.rChild.data)>0) {
						node=leftRotate(node);
					}else
						node=rightLeftRotate(node);
					}	
				}
			node.height= 1+Math.max(height(node.lChild), height(node.rChild));
			return node;
			
		}
		public T minVal(AVLNode node) {
			AVLNode curr= node;
			while(curr.lChild!=null) {
				curr=curr.lChild;
			}
			return curr.data;
		}
		public AVLNode delete(AVLNode node, T key) {
			if (node==null)
				return node;
			
			if(key.compareTo(node.data)<0) 
				node.lChild= delete(node.lChild,key);
			else if (key.compareTo(node.data)>0) 
				node.rChild= delete(node.rChild,key);
			else {
				found=true;
				if ((node.lChild==null)||(node.rChild==null)) {
					AVLNode tmp= null;
					if (tmp==node.lChild) 
						tmp=node.rChild;
					else 
						tmp= node.lChild;
					
					if (tmp==null) {
						tmp= node;
						node=null;
					}else
						node=tmp;
				}
				else {
					T tmp= minVal(node.rChild);
					node.data=tmp;
					node.rChild= delete(node.rChild, tmp);
				}
			}
			if (node==null) {
				return node;
			}
			
			node.height= 1+Math.max(height(node.lChild), height(node.rChild));
			int balance= getBalance(node);
			
			if(balance<-imbalance&&getBalance(node.rChild)<=0) {
				return leftRotate(node);
			}
			
			if (balance<-imbalance&&getBalance(node.rChild)>0) {
				return rightLeftRotate(node);
			}
			
			if(balance > imbalance&&getBalance(node.lChild)>=0) {
				return rightRotate(node);
			}
			if (balance>imbalance&&getBalance(node.lChild)<0) {
				return leftRightRotate(node);
			}
			
			
			return node;
		}
		
	   public int getBalance(AVLNode node) {
		   return height(node.lChild)-height(node.rChild);
		   }
	}

    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
       if (maxImbalance<1) {
    	   throw new InvalidBalanceException("Balance must be greater than 1");
       }
       found=false;
       root=null;
       size=0;
       this.imbalance=maxImbalance;
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * @param key The key to insert in the tree.
     */
    
    public void insert(T key) {
        if (isEmpty()) {
        	root= new AVLNode(key); 
        }else {
        	root=root.insert(root, key);
        }
        	size++;       
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
    	found=false;
    	AVLNode result;
       if(isEmpty()) {
    	   throw new EmptyTreeException("Error: Tree is empty");       
    	}else
    		result= root.delete(root, key);
    		if(found) {
    		root = result;
    		size--;
       		return key;
       		}else
       		return null;     		
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
    	if (this.isEmpty()) {
    		throw new EmptyTreeException("error: tree empty");    		
    	}else
    		return root.search(key);
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
    	return imbalance;
    }

    public int height(AVLNode node) {
    	if(node==null) {
    		return -1;
    	}else {
    		return node.height;
    	}
	}
    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
    	return height(root);
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
    	return root==null;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	if(isEmpty()) {
    		throw new EmptyTreeException("error: empty tree");
    	}
    	return root.data;
    }
    public void insertNoBSTTest(T key) {
    	AVLNode curr=null;
    	if (isEmpty()) {
    		root= new AVLNode(key);
    	}else {
    	curr= root;
    	while (curr.lChild!=null) {
    		curr=curr.lChild;
    	}
    	curr.lChild= new AVLNode(key);
    	}
    }
    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
    	if(isEmpty()) {
    		return true;
    	}
        return isBSTAux(root);       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }
    public boolean isBSTAux(AVLNode node){
    	if (node.lChild==null&&node.rChild==null) {
    		return true;
    	}else if (node.lChild==null&&node.rChild.data.compareTo(node.data)>0) {
    		return isBSTAux(node.rChild);
    	}else if (node.rChild==null&&node.lChild.data.compareTo(node.data)<0) {
    		return isBSTAux(node.lChild);
    	}else if (node.rChild!=null&&node.lChild!=null) {
    		if (node.lChild.data.compareTo(node.data)<0 &&node.rChild.data.compareTo(node.data)>0) {
    			return isBSTAux(node.lChild)&& isBSTAux(node.rChild);
    		}
    	}
    	return false;
    }
    
    
    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
    	if(isEmpty()) {
    		return true;
    	}
        return AVLGBalancedAux(root);
    }
    public boolean AVLGBalancedAux(AVLNode node) {
    	if(node==null) {
    		return true;
    	}
    	int lHeight= height(node.lChild);
    	int rHeight= height(node.rChild);
    	if(Math.abs(lHeight-rHeight)<=imbalance&&AVLGBalancedAux(node.lChild)&&AVLGBalancedAux(node.rChild)) {
    	return true;
    	}
    	return false;    	
    }
    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
        root=null;
        size=0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
    	return size;
    }
}
