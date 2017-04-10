package edu.fandm.ztang.insightfm.Models;

import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by zhuofantang on 4/6/17.
 *
 * This is a inforsearchmodel for other models. It implements:
 * 1. A Trie class (used for word search, like class title, building, instructor, etc.)
 * 2. A
 */

public class InfoSearchModel {


    /**
     * A Parent class of trienode
     */
    public static class TrieNode{
        protected TrieNode[] nodes;

        public boolean isEnd;

        private int infoID;

        public TrieNode getNodeAtIndex(int index){
            return nodes[index];
        }

        public int getInfoID(){
            return infoID;
        }

        public void setNodeAtIndex(int index, TrieNode tmp){
            nodes[index] = tmp;
        }

        public void setInfoID(int infoID) {
            this.infoID = infoID;
        }
    }

    /**
     * A sub-class of trienode that is for number search
     */
    public static class NumberTrieNode extends TrieNode{

        public NumberTrieNode(int INFOID){
            this.nodes = new NumberTrieNode[10];
            this.setInfoID(INFOID);
        }

    }

    /**
     * A sub-class of trienode that is for word search
     */
    public static class WordTrieNode extends TrieNode{

        public WordTrieNode(int INFOID) {
            this.nodes = new WordTrieNode[46];
            this.setInfoID(INFOID);
        }

    }

    /**
     * A class Trie that implement a trie structure
     */
    public static class Trie {
        private TrieNode root;
        private boolean isNum;

        public Trie(boolean pisNum,int INFOID ) {

            isNum = pisNum;

            if(isNum){
                root  = new NumberTrieNode(INFOID);
            }else{
                root = new WordTrieNode(INFOID);
            }
        }

        // Inserts a word into the trie.
        public void insert(String s, int INFOID) {
            s = s.toLowerCase();
            TrieNode p = root;
            for(int i=0; i<s.length(); i++){
                char c= s.charAt(i);
                int index;
                if(Character.isDigit(c)){

                    if(isNum){
                        index = Character.getNumericValue(c);
                    }else{
                        index = 36 + Character.getNumericValue(c);
                    }
                    //Log.d("Error Integer Parsing: ", s + "    " + String.valueOf(c) + "  " + String.valueOf(index));
                }else{
                    index = c-'a';

                    //checking for special characters
                    if(String.valueOf(c).equals(" ")){
                        index = 26;
                    }
                    if(String.valueOf(c).equals("-")){
                        index = 27;
                    }
                    if(String.valueOf(c).equals("'")){
                        index = 28;
                    }
                    if(String.valueOf(c).equals("&")){
                        index = 29;
                    }
                    if(String.valueOf(c).equals(":")){
                        index = 30;
                    }
                    if(String.valueOf(c).equals(",")){
                        index = 31;
                    }
                    if(String.valueOf(c).equals("\"")){
                        index = 32;
                    }
                    if(String.valueOf(c).equals("/")){
                        index = 33;
                    }
                    if(String.valueOf(c).equals(".")){
                        index = 34;
                    }
                    if(String.valueOf(c).equals("?")){
                        index = 35;
                    }
                }
                Log.d("Error Insert: ", s + "     " + c + "     " +  String.valueOf(index));
                if(p.getNodeAtIndex(index)==null){
                    TrieNode temp;
                    if(isNum){
                        temp = new NumberTrieNode(INFOID);
                    }else{
                        temp = new WordTrieNode(INFOID);
                    }
                    p.setNodeAtIndex(index, temp);
                    p = temp;
                }else{
                    p = p.getNodeAtIndex(index);
                }
            }
            p.isEnd =true ;
            //Log.d("P's INFOID: ", String.valueOf(p.isEnd) + "    " + String.valueOf(p.getInfoID()));
        }

        // Returns if the word is in the trie.
        public boolean search(String word) {
            TrieNode p = searchNode(word);
            if(p==null){
                //Log.d("TrieNode Null: ", "Yes!");
                return false;
            }else{
                if(p.isEnd)
                    return true;
            }

            return false;
        }

        // Returns if there is any word in the trie
        // that starts with the given prefix.
        public boolean startsWith(String prefix) {
            TrieNode p = searchNode(prefix);
            if(p==null){
                return false;
            }else{
                return true;
            }
        }

        public TrieNode searchNode(String s){
            s = s.toLowerCase();
            TrieNode p = root;
            for(int i=0; i<s.length(); i++){
                char c= s.charAt(i);
                int index;
                if(Character.isDigit(c)){

                    if(isNum){
                        index = Character.getNumericValue(c);
                    }else{
                        index = 35 + Character.getNumericValue(c);
                    }
                }else{
                    index = c-'a';

                    //checking for special characters
                    if(String.valueOf(c).equals(" ")){
                        index = 26;
                    }
                    if(String.valueOf(c).equals("-")){
                        index = 27;
                    }
                    if(String.valueOf(c).equals("'")){
                        index = 28;
                    }
                    if(String.valueOf(c).equals("&")){
                        index = 29;
                    }
                    if(String.valueOf(c).equals(":")){
                        index = 30;
                    }
                    if(String.valueOf(c).equals(",")){
                        index = 31;
                    }
                    if(String.valueOf(c).equals("\"")){
                        index = 32;
                    }
                    if(String.valueOf(c).equals("/")){
                        index = 33;
                    }
                    if(String.valueOf(c).equals(".")){
                        index = 34;
                    }
                    if(String.valueOf(c).equals("?")){
                        index = 35;
                    }
                }


                //Log.d("Error Search: ", s + "     " + c + "     " +  String.valueOf(index));
                if(p.getNodeAtIndex(index)!=null){
                    p = p.getNodeAtIndex(index);
                }else{
                    return null;
                }
            }
            Log.d("P's INFOID: ", String.valueOf(p.isEnd) + "    " + String.valueOf(p.getInfoID()));


            if(p==root){
                //Log.d("Error: ", "P is root");
                return null;
            }



            return p;
        }


        //A Arraylist to store possible result of a word search
        private ArrayList<Integer> possibleSearchResult;

        //A method to search all possible search result according to current searchword
        public ArrayList<Integer> searchAllPossibleResult(String searchWord){

            //initialize the output arraylist
            possibleSearchResult = new ArrayList<>();

            //get the start trienode
            final TrieNode startNode = searchNode(searchWord);

            //TODO use better algorithm to get rid of repetitive work
            searchAllPossibleResultHelper(startNode);
            return possibleSearchResult;
        }

        //A helper function, recursively get possible result
        private void searchAllPossibleResultHelper(TrieNode currentNode){

            //check if the current is null or not
            if(currentNode != null){

                //check if the currentNode is a end, then add its InfoID to the search results
                if(currentNode.isEnd && currentNode.getInfoID() != -1){
                    possibleSearchResult.add(currentNode.getInfoID());
                }

                //check all possible branches
                for(int i = 0; i < currentNode.nodes.length; i++){
                    TrieNode tempNode = currentNode.getNodeAtIndex(i);
                    if(tempNode != null){
                        searchAllPossibleResultHelper(tempNode);
                    }
                }
            }

        }
    }








}
