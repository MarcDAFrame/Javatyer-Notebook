$(document).ready(function () {
    let cells = []
    let cell_count = 1;
    let twice_68 = false;    
    init();
    function refresh() {
        textarea();
        init_cells();
    }
    function init() {
        init_header();
        textarea();
        init_cells();
        commands();
        init_modals();
    }
    function new_file(){
        $("#content").children().each(function(){
            $(this).remove();
        });
        append_cell();
        select_first_cell();
    }
    function load_cells(cell_json){
        $("#content").children().each(function(){
            $(this).remove();
        });      
        $.each(cell_json['cells'], function(index, element){
            console.log(index, element)
            console.log(element);
            append_cell(element);
        });saves
        select_first_cell();
    }

    function init_modals(){
        $("#load").click(function(){
            console.log("test");
            $("#load-modal").css("display", "block")
        });
        $("#load-close").click(function(){
            $("#load-modal").css("display", "none")
        });
        $("#loadfilebtn").click(function(){
            $("#load-modal").css("display", "none")            
            let filename = $("#loadfiletxt").val();
            let request_json = {"file" : filename};
            $.ajax({
                url: '/load',
                type: 'POST',
                data: JSON.stringify(request_json),
                async: true,
                success: function (msg) {
                    let json = $.parseJSON(msg);
                    console.log(json);
                    load_cells(json);
                },
                error: function (e) {
                    console.log('error while saving');
                }
            });
        });


        $("#save").click(function(){
            console.log("test");
            $("#save-modal").css("display", "block")
        });
        $("#load-close").click(function(){
            $("#save-modal").css("display", "none")
        });
        $("#savebtn").click(function(){
            $("#save-modal").css("display", "none")            
            let filename = $("#filenametxt").val();
            let directory = $("#directorytxt").val();
            let save_code = {}
            save_code['cells']={}
            $(".cell").each(function (index){
                // console.log(index);
                save_code['cells']['cell'+index.toString()] = $(this).children("div.in").children("textarea").val()
            });
            console.log("sending code to be saved", save_code)
            save_code['directory']=directory;
            save_code['filename']=filename;
            $.ajax({
                url: '/save',
                type: 'POST',
                data: JSON.stringify(save_code),
                async: true,
                success: function (msg) {
                    // let json = $.parseJSON(msg)
    
                },
                error: function (e) {
                    console.log('error while saving');
                }
            });

        });
    }

    function append_cell(cell_data = ""){
        let cell_html = `<div class="cell" id="">
        <div class="in">
            <textarea style="width:100%" rows=5 name="ide" form="code" class="cell-text">`+cell_data+`</textarea>
        </div>
        <div class="out">
            <p class="out-console"></p>
            <p class="out-output"></p>
            <p class="out-debug"></p>
        </div>
    </div>
        `
        $("#content").append(cell_html);
        
        refresh();
        cell_count += 1;
    }
    function delete_cell(cell){
        if(cell_count == 1){
            cell.children("div.in").children("textarea").text("");
        }else{
            if(select_prev_cell($("#cell-selected"))){
                cell.remove();
                refresh();
                cell_count -= 1;
            }else if(select_next_cell($("#cell-selected"))){
                cell.remove();
                refresh();
                cell_count -= 1;
                
            }else{
                console.log('IMPOSSIBLE')
            }
        }
    }
    function init_header() {

        $("#insert-main-method").click(function () {
            // console.log($('#cell-selected>div.in>textarea'));
            $('#cell-selected>div.in>textarea').append(`public class Run{
    public static String Run(){
        

        
    }
}`);
            refresh();
        });

        //add cell button clicked
        $('#add-cell').click(function () {
            append_cell();
        });

        //run button clicked
        $('#run').click(function(){
            console.log('run');
            run_code($('#cell-selected'));
        })

        $('#newfile').click(function(){
            console.log('newfile');
            // run_code($('#cell-selected'));
            new_file();
        })
    }

    function commands(){
        $(document).keydown(function(e) {
            if (e.keyCode == 13 && e.ctrlKey && e.shiftKey) { // ctrl + sxthift + enter
                if($('#cell-selected').next().attr("class") === "cell"){
                    run_code($('#cell-selected'));
                    select_next_cell($("#cell-selected"));
                }else{
                    append_cell();
                    run_code($('#cell-selected'));
                    select_next_cell($("#cell-selected"));
                }
            }
            if (e.keyCode == 13 && e.ctrlKey) { // ctrl + enter
                run_code($('#cell-selected'));
            }

            if(e.keyCode ==  66 && !$("#cell-selected>div.in>textarea:focus").length > 0){ // b + focus is not on the textarea
                append_cell();
                select_next_cell($("#cell-selected"));
            }
            if(e.keyCode == 68 && !$("#cell-selected>div.in>textarea:focus").length > 0){ // d + d within 300ms + focus is not on the textarea
                if(twice_68){
                    delete_cell($("#cell-selected"))
                    twice_68 = false;
                }
                twice_68 = true;
                setTimeout(function(){ // ...reset to false after 300ms
                    twice_68 = false; 
                }, 300);
            }
        });
    }
    function run_code(cell){
        let text_data = cell.children('div.in').children('textarea.cell-text').val();
        if (!text_data) return;
        $.ajax({
            url: '/run',
            type: 'POST',
            data: text_data,
            async: true,
            success: function (msg) {
                let json = $.parseJSON(msg)
                // if (json.output){let output = json.output.replace("\n", "<br>");}else{output=json.output}
                // if (json.output){ let outconsole = json.console.replace("\n", "<br>");}else{outconsole=json.output}

                cell.children("div.out").children("p.out-console").text("");
                cell.children("div.out").children("p.out-output").text("");
                cell.children("div.out").children("p.out-debug").text("");
                select_first_cell
                cell.children("div.out").children("p.out-console").text(json.console);
                cell.children("div.out").children("p.out-output").text(json.output);
                cell.children("div.out").children("p.out-debug").text(json.debug);

            },
            error: function (e) {
                console.log('error');
            }
        });
    }


    function select_prev_cell(cell){
        // console.log("select_prev_cell")
        // console.log(cell.prev());
        if(cell.prev().attr("class") === "cell"){
            highlight_cell(cell.prev()); 
            return true
        }else{
            return false;
        }
    }
    
    function select_next_cell(cell){
        // console.log("select_next_cell")
        // console.log(cell.next());
        if(cell.next().attr("class") === "cell"){
            highlight_cell(cell.next()); 
            return true
        }else{
            return false;
        }
    }
    function select_first_cell(){
        highlight_cell($("#content").children().first());
    }

    function highlight_cell(cell){
        $('.cell.cell-highlight').not(this).attr('id', '');
        $('.cell.cell-highlight').not(this).removeClass('cell-highlight');
        cell.addClass('cell-highlight');
        cell.attr('id', 'cell-selected');
    }
    
    function init_cells() {
        $('.cell').each(function (index) {
            $(this).off('click');
            $(this).click(function (index) {
                highlight_cell($(this));
            })
        })

    }

    function textarea() {
        $('textarea').each(function (index) {
            var rows = $(this).val().split("\n");
            if(rows.length < 7){
                $(this).prop('rows', 7);
            }else{
                $(this).prop('rows', rows.length);
            }
 
            $(this).keyup(function (e) {
                var rows = $(this).val().split("\n");                
                if(rows.length < 7){
                    $(this).prop('rows', 7);
                }else{
                    $(this).prop('rows', rows.length);                    
                }
                
            });
        })
    }




});