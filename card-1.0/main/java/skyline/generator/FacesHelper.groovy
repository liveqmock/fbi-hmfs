package skyline.generator

import groovy.sql.Sql

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-18
 * Time: ÉÏÎç7:51
 * To change this template use File | Settings | File Templates.
 */
class FacesHelper {
    def local = [url: 'jdbc:oracle:thin:@localhost:1521:orcl', user: 'hmfs', password: 'hmfs', driver: 'oracle.jdbc.driver.OracleDriver']
    def localdb = Sql.newInstance(local.url, local.user, local.password, local.driver)

    static void main(args) {
        FacesHelper helper = new FacesHelper();
        //helper.exportViewFacesXhtml("his_msgin_log")
        helper.exportEditableFacesXhtml("his_msgin_log")
    }

    def exportViewFacesXhtml(String tblname) {
        tblname = tblname.toLowerCase()
        def sql = """
            SELECT lower(t.column_name), trim(substr(t1.comments,instr(t1.comments, '£º') + 1, 8))
              FROM user_tab_cols t, user_col_comments t1
             WHERE t.table_name = t1.table_name
               AND t.column_name = t1.column_name(+)
               and lower(t.table_name) = '${tblname}'
             order by substr(t1.comments, 1, 1),
                      to_number(substr(t1.comments, 2, instr(t1.comments, '£º') - 2))
        """
        localdb.rows(sql).each {Map row ->
            def buffer = row.collect {k, v -> v == null ? '' : "$v"}.join(';')
            //println buffer
            def dbFieldName = buffer.split(";")[0]
            def fieldName = dbFieldName.split("_").collect {
                it[0].toUpperCase() + (it.size() > 1 ? it[1..-1] : '')
            }.join('')
            fieldName = fieldName[0].toLowerCase() + fieldName[1..-1]

            def headerText = buffer.split(";")[1].trim()
            println("""<p:column headerText="${headerText}"><h:outputText value="#{record.${fieldName}}"/></p:column>""")
        }

    }
    def exportEditableFacesXhtml(String tblname) {
        tblname = tblname.toLowerCase()
        def sql = """
            SELECT lower(t.column_name), trim(substr(t1.comments,instr(t1.comments, '£º') + 1, 8))
              FROM user_tab_cols t, user_col_comments t1
             WHERE t.table_name = t1.table_name
               AND t.column_name = t1.column_name(+)
               and lower(t.table_name) = '${tblname}'
             order by substr(t1.comments, 1, 1),
                      to_number(substr(t1.comments, 2, instr(t1.comments, '£º') - 2))
        """
        localdb.rows(sql).each {Map row ->
            def buffer = row.collect {k, v -> v == null ? '' : "$v"}.join(';')
            //println buffer
            def dbFieldName = buffer.split(";")[0]
            def fieldName = dbFieldName.split("_").collect {
                it[0].toUpperCase() + (it.size() > 1 ? it[1..-1] : '')
            }.join('')
            fieldName = fieldName[0].toLowerCase() + fieldName[1..-1]

            def headerText = buffer.split(";")[1].trim()
            println("""<p:column headerText="${headerText}">
        <p:cellEditor><f:facet name="output"><h:outputText value="#{record.${fieldName}}" />
            </f:facet><f:facet name="input"><p:inputText value="#{record.${fieldName}}" label="${fieldName}"/></f:facet>
        </p:cellEditor>
    </p:column>""")
        }

    }

}
