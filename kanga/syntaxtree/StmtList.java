//
// Generated by JTB 1.3.2
//

package kanga.syntaxtree;

/**
 * Grammar production:
 * f0 -> ( ( Label() )? Stmt() )*
 */
public class StmtList implements Node {
   public NodeListOptional f0;

   public StmtList(NodeListOptional n0) {
      f0 = n0;
   }

   public void accept(kanga.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(kanga.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(kanga.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(kanga.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

