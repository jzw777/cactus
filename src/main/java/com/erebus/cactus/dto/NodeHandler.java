package com.erebus.cactus.dto;

import java.util.Stack;

public class NodeHandler<T> {


    public  Node<T>  generateNode(int n  ){

        if (n==0){
            return null;
        }
        if (n==1){
            return  new Node<T>();
        }
        Node<T> root = new Node<>();
        //n = 3
        Stack<Node> stack = new Stack<>();

        stack.push(root);
        for (int i = 0; i < n; i++) {
            Stack<Node> stack2 = new Stack<>();
            while (!stack.isEmpty()){

                Node pop = stack.pop();
                Node<Integer> left = new Node<>();
                Node<Integer> right = new Node<>();
                left.parent = pop;
                right.parent= pop;
                pop.left = left;
                pop.right = right;
                stack2.push(left);
                stack2.push(right);
            }
            stack = stack2;
        }

        return root;
    }

    public Node<T> generateTree(int n){
        //鲁棒性

        if (n==0){
            return null;
        }
        if (n==1){
            return new Node<>();
        }
        Node<T> root = new Node<>();
        n--;
        root.left = generateTree(n);
        root.right = generateTree(n);

        root.left.parent = root;
        root.right.parent = root;


        return root;
    }

    public static void main(String[] args) {
        Node<Integer> integerNode = new NodeHandler<Integer>().generateTree(3);
        System.out.println(1);
    }


}
