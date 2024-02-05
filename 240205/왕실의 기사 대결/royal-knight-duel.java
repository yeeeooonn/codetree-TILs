import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int L;
    static int[][] map;
    static int[][] person;
    static Knight[] knights;
    static int[] firstScore;
    static boolean[] isMeet;
    static boolean isWall;
    static int answer;
    static int[] dr = {-1, 0, 1, 0}; //위 오 아 왼
    static int[] dc = {0, 1, 0, -1}; //위 오 아 왼
    static class Knight { //위치(r,c), 구간(h,w), 체력(k)
        int r;
        int c;
        int h;
        int w;
        int k;
        public Knight(int r, int c, int h, int w, int k) {
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
        }
    }
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        int N = Integer.parseInt(st.nextToken());
        int Q = Integer.parseInt(st.nextToken());

        map = new int[L][L]; //맵에 관한 정보(빈칸, 함정, 벽)
        person = new int[L][L]; //기사 위치에 관한 정보
        knights = new Knight[N+1];
        firstScore = new int[N+1];

        for (int i = 0; i < L; i++) { //맵 입력
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < L; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 1; i <= N; i++) { //기사 정보 입력
            st = new StringTokenizer(br.readLine());
            knights[i] = new Knight(Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
            firstScore[i] = knights[i].k;
            for (int j = knights[i].r; j < knights[i].r + knights[i].h; j++) {
                for (int k = knights[i].c; k < knights[i].c + knights[i].w; k++) {
                    person[j][k] = i;
                }
            }
        }
        for (int i = 0; i < Q; i++) { //기사 정보 입력
            st = new StringTokenizer(br.readLine());
            isWall = false;
            isMeet = new boolean[N+1];
            int k = Integer.parseInt(st.nextToken());
            push(k, k, Integer.parseInt(st.nextToken()));
        }

        for (int i = 1; i <= N; i++) {
            if(knights[i].k > 0) {
                answer += (firstScore[i] - knights[i].k);
            }
        }
        System.out.println(answer);

        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                System.out.print(person[i][j]+" ");
            }
            System.out.println();
        }
    }
    static void push(int first, int knightNum, int d) {
        //명령받은 기사가 체력이 1 이상인지 확인 후 진행
        if(knights[knightNum].k <= 0) return;

        // 나이트 + direction위치에 있는 구간에 기사 있는지 확인
        if(d == 0) { //위 -> 가로 길이 확인
            for (int i = 0; i < knights[knightNum].w; i++) {
                int nr = knights[knightNum].r + dr[d];
                int nc = knights[knightNum].c + i + dc[d];
                checkKnight(nr,nc, first, d);
            }


        }else if(d == 2) { //아래 -> 가로 길이 확인
            for (int i = 0; i < knights[knightNum].w; i++) {
                int nr = knights[knightNum].r + knights[knightNum].h -1 + dr[d];
                int nc = knights[knightNum].c + i + dc[d];
                checkKnight(nr,nc, first, d);
            }

        }else if(d == 1) { //오, 왼 -> 세로 길이 확인
            for (int i = 0; i < knights[knightNum].h; i++) {
                int nr = knights[knightNum].r + i + dr[d];
                int nc = knights[knightNum].c + knights[knightNum].w -1 + dc[d];
                checkKnight(nr,nc, first, d);
            }

        }else {//왼쪽일 때
            for (int i = 0; i < knights[knightNum].h; i++) {
                int nr = knights[knightNum].r + i + dr[d];
                int nc = knights[knightNum].c + dc[d];
                checkKnight(nr,nc, first, d);
            }


        }
        //다 확인 했으면 옮기기 - 벽을 안만났을 경우에만
        if(!isWall) {
            move(knightNum, d, first == knightNum);
        }
    }
    static void move(int knightNum, int d, boolean isFirst) {

        //원래있던곳 0으로
        for (int j = knights[knightNum].r; j < knights[knightNum].r + knights[knightNum].h; j++) {
            for (int k = knights[knightNum].c; k < knights[knightNum].c + knights[knightNum].w; k++) {
                person[j][k] = 0;
            }
        }
        knights[knightNum].r += dr[d];
        knights[knightNum].c += dc[d];

        //옮기기 & 점수 측정
        for (int j = knights[knightNum].r; j < knights[knightNum].r + knights[knightNum].h; j++) {
            for (int k = knights[knightNum].c; k < knights[knightNum].c + knights[knightNum].w; k++) {
                person[j][k] = knightNum;
                if(!isFirst && map[j][k] == 1) {
                    knights[knightNum].k -=1;
                }
            }
        }
        //점수 없는 사람이면 맵에서 제거
        if(knights[knightNum].k < 1) {
            for (int j = knights[knightNum].r; j < knights[knightNum].r + knights[knightNum].h; j++) {
                for (int k = knights[knightNum].c; k < knights[knightNum].c + knights[knightNum].w; k++) {
                    person[j][k] = 0;
                }
            }

        }
    }
    static void checkKnight(int nr, int nc, int first, int d) {
        if(!check(nr,nc) || map[nr][nc] == 2) {
            //벽 만남
            isWall = true;
            return;
        }
        if(person[nr][nc] > 0 && !isMeet[person[nr][nc]]){
            //다른 기사가 있다면 + 만나지 않았던 기사라면 재귀
            isMeet[person[nr][nc]] = true;
            push(first, person[nr][nc], d);
        }
    }

    static boolean check(int nr, int nc) {
        return nr>=0 && nr<L && nc>=0 && nc<L;
    }
}