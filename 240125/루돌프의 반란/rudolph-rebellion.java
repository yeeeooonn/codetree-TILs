import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int N;
    static int M;
    static int P;
    static int C;
    static int D;
    static int[] score;
    static int[] stop;
    static boolean[] isFail;
    static int[][] map;
    static L[] location;
    static int[] dr = {-1,-1,-1,0,1,1,1,0}; //왼쪽위대각선부터 시계방향
    static int[] dc = {-1,0,1,1,1,0,-1,-1};
    static class L {
        int r;
        int c;
        public L(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    public static void main(String[] args)throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder();

        N = Integer.parseInt(st.nextToken()); //게임판 크기
        M = Integer.parseInt(st.nextToken()); //턴 수
        P = Integer.parseInt(st.nextToken()); //산타 수
        C = Integer.parseInt(st.nextToken()); //루돌프 힘
        D = Integer.parseInt(st.nextToken()); //산타 힘

        map = new int[N][N];
        location = new L[P+1]; //루돌프와 산타의 위치좌표

        //점수판
        score = new int[P+1];

        //산타 행동 금지
        stop = new int[P+1];

        //탈락한 산타
        isFail = new boolean[P+1];

        //루돌프 위치 -1로 표시
        st = new StringTokenizer(br.readLine());
        int lr = Integer.parseInt(st.nextToken())-1;
        int lc = Integer.parseInt(st.nextToken())-1;
        map[lr][lc] = -1;
        location[0] = new L(lr,lc);


        for (int i = 0; i < P; i++) {
            //산타 위치 1~P로 표시
            st = new StringTokenizer(br.readLine());
            int santaN = Integer.parseInt(st.nextToken());
            int sr = Integer.parseInt(st.nextToken())-1;
            int sc = Integer.parseInt(st.nextToken())-1;
            map[sr][sc] = santaN;
            location[santaN] = new L(sr,sc);

        }

        for (int i = 0; i < M; i++) {
            //루돌프 이동
            //표적 산타를 찾음
            int closeS = Integer.MAX_VALUE;
            int targetS = -1;
            for (int j = 1; j <= P; j++) {
                if(isFail[j]) continue;
                int distance = (int) Math.pow(location[0].r - location[j].r,2) + (int) Math.pow(location[0].c - location[j].c,2);
                if(distance < closeS) {
                    closeS = distance;
                    targetS = j;
                } else if (distance == closeS && location[j].r >location[targetS].r) {
                    targetS = j;
                } else if (distance == closeS && location[j].r == location[targetS].r && location[j].c > location[targetS].c) {
                    targetS = j;
                }
            }

            if(targetS != -1) {
                //루돌프 방향 정하기(타겟 산타와 가까운순)
                int closeD = Integer.MAX_VALUE;
                int targetD = -1;
                for (int d = 0; d < 8; d++) {
                    int nr = location[0].r + dr[d];
                    int nc = location[0].c + dc[d];
                    int dis = (int)Math.pow(nr - location[targetS].r,2)+(int)Math.pow(nc - location[targetS].c,2);
                    if(dis < closeD) {
                        closeD =dis;
                        targetD = d;
                    }
                }

                if(map[location[0].r + dr[targetD]][location[0].c + dc[targetD]] != 0) {
                    //루돌프 -> 산타 충돌 시 작용
                    int santaNum = map[location[0].r + dr[targetD]][location[0].c + dc[targetD]];
                    score[santaNum] += C;
                    stop[santaNum] = 2;

                    //루돌프 자리 옮김
                    map[location[0].r][location[0].c] = 0;
                    location[0].r += dr[targetD];
                    location[0].c += dc[targetD];
                    map[location[0].r][location[0].c] = -1;

                    
                    //산타가 밀려난 자리
                    int nr = location[0].r;
                    int nc = location[0].c;
                    for (int j = 0; j < C; j++) {
                        nr += dr[targetD];
                        nc += dc[targetD];
                    }
                    
                    if(!check(nr,nc)) { //범위 밖으로 밀려나면 탈락
                        isFail[santaNum] = true;
                        location[santaNum].r = -1000;
                        location[santaNum].c = -1000;
                    } else {
                        if(map[nr][nc] != 0) {
                            //다른 산타가 있는 경우
                            pushSanta(nr,nc, santaNum, targetD);

                        }else {
                            //산타 저장
                            map[nr][nc] = santaNum;
                            location[santaNum].r = nr;
                            location[santaNum].c = nc;
                        }
                    }
                }else {
                    //루돌프 위치만 옮기기
                    map[location[0].r][location[0].c] = 0;
                    location[0].r += dr[targetD];
                    location[0].c += dc[targetD];
                    map[location[0].r][location[0].c] = -1;

                }


            }
            
            
            //산타 이동
            for (int j = 1; j <= P ; j++) {
                if(isFail[j]) continue;
                if(stop[j] > 0) {
                    stop[j]--;
                    continue;
                }

                
                //이동할 방향 정하기
                int sr = location[j].r;
                int sc = location[j].c;


                int disD = (int)Math.pow(sr - location[0].r,2)+(int)Math.pow(sc - location[0].c,2);
                int targetD = -1; //이동할 방향
                for (int d = 1; d < 8; d+=2) {
                    int nr = sr + dr[d];
                    int nc = sc + dc[d];
                    if(!check(nr,nc)) continue;
                    int dis = (int)Math.pow(nr - location[0].r,2)+(int)Math.pow(nc - location[0].c,2);
                    if((dis < disD && map[nr][nc] == 0)|| map[nr][nc] == -1) {
                        disD =dis;
                        targetD = d;
                    }
                }

                //이동
                if(targetD!= -1) {
                    if(map[sr+dr[targetD]][sc+dc[targetD]] == -1) {
                        //루돌프가 있는 경우
                        //내위치 지우기(산타)
                        map[location[j].r][location[j].c] = 0;

                        //점수 얻기
                        score[j] += D;
                        stop[j] = 1;

                        //산타 d+4 mod8 만큼 밀림
                        int changeD = (targetD+4)%8;

                        int nr = location[0].r;
                        int nc = location[0].c;
                        for (int d = 0; d < D; d++) {
                            nr += dr[changeD];
                            nc += dc[changeD];
                        }


                        if(!check(nr,nc)) { //범위 밖으로 밀려나면 탈락
                            map[location[j].r][location[j].c] = 0;
                            isFail[j] = true;
                            location[j].r = -1000;
                            location[j].c = -1000;
                        } else {
                            if(map[nr][nc] != 0) {

                                //다른 산타가 있는 경우
                                pushSanta(nr,nc, j, changeD);

                            }else {
                                //산타 저장
                                map[nr][nc] = j;
                                location[j].r = nr;
                                location[j].c = nc;
                            }

                        }


                    }else {
                        //빈칸인 경우
                        location[j].r += dr[targetD];
                        location[j].c += dc[targetD];
                        map[sr][sc] = 0;
                        map[sr+dr[targetD]][sc+dc[targetD]] = j;
                    }

                }

            }



            //마지막 행동
            //살아있는 산타 점수 추가해주기
            for (int j = 1; j <=P ; j++) {
                if(!isFail[j]) {
                    score[j] +=1;
                }
            }
        }

        for (int i = 1; i <= P; i++) {
            sb.append(score[i]).append(" ");
        }
        System.out.println(sb);


    }
    static boolean check(int nr, int nc) {
        return nr>=0 && nr<N && nc>=0 && nc<N;
    }
    static void pushSanta(int nr, int nc, int santaNum, int targetD) {
        int keepSanta = map[nr][nc];
        map[nr][nc] = santaNum;
        location[santaNum].r = nr;
        location[santaNum].c = nc;

        while(true) {
            if(!check(nr+dr[targetD], nc+dc[targetD])) {
                //벗어났으면 keep 산타 탈락처리
                isFail[keepSanta] = true;
                location[keepSanta].r = -1000;
                location[keepSanta].c = -1000;
                break;
            }

            int keep2Santa = map[nr+dr[targetD]][nc+dc[targetD]];
            map[nr+dr[targetD]][nc+dc[targetD]] = keepSanta;
            location[keepSanta].r = nr+dr[targetD];
            location[keepSanta].c = nc+dc[targetD];
            keepSanta = keep2Santa;

            if(keepSanta == 0) break; //아무도없는 공간이면 종료

            nr += dr[targetD];
            nc += dc[targetD];

        }


    }
}