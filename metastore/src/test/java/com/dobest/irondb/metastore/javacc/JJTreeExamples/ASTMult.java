/* Generated By:JJTree: Do not edit this line. ASTMult.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.dobest.irondb.metastore.javacc.JJTreeExamples;

public
class ASTMult extends SimpleNode {
  public ASTMult(int id) {
    super(id);
  }

  public ASTMult(Eg4 p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(Eg4Visitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c89dab7516ffaeb5fa9e27ee910f6325 (do not edit this line) */
